package forest;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputAdapter;
import javax.swing.text.View;

import utility.*;

/**
 * 樹状整列におけるMVCのコントローラ（C）を担うクラスになります。
 */
public class ForestController extends MouseInputAdapter implements MouseWheelListener
{
    /**
     * 樹状整列におけるMVCのモデル（M）を記憶するフィールドです。
     */
    private ForestModel model;

    /**
     * 樹状整列におけるMVCのビュー（V）を記憶するフィールドです。
     */
    private ForestView view;

    /**
     * マウスの直近の位置を記憶するフィールドです。
     */
    private Point previous;

    /**
     * マウスの現在の位置を記憶するフィールドです。
     */
    private Point current;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     */
    public ForestController()
    {
        super();
        this.model = null;
		this.view = null;
		this.previous = null;
		this.current = null;
		return;
    }

    /**
     * 指定されたマウスイベントからマウスカーサの位置を獲得して、
     * モデル座標系でのクリック位置を割り出して標準出力に出力する。
     * さらに、クリックされたノードまたはリーフの名前を標準出力に出力する。
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mouseClicked(MouseEvent aMouseEvent)
    {
        // 画面上のクリック位置を取得
        Point screenPoint = aMouseEvent.getPoint();

        // ビューのスクロール量を考慮してモデル座標に変換
        Point modelPoint = new Point(screenPoint.x + view.scrollAmount().x,
                                     screenPoint.y + view.scrollAmount().y);
		

        // デバッグ用にモデル座標を出力
        System.out.println("Clicked at Model Coordinates: " + modelPoint);

        // Viewがnullでないことを確認し、その後の処理を実行
        // Condition.ifTrue() を使って this.view が null でない場合に処理を実行
        Condition.ifTrue(() -> this.view != null, () -> {
            java.util.Optional<Node> nodeOrLeafName = this.view.whichOfNodes(modelPoint);
			//Node nodeOrLeafName = this.view.whichOfNodes(screenPoint);
            // 名前が見つかった場合と見つからなかった場合の処理
            Condition.ifThenElse(() -> nodeOrLeafName.isPresent(), /*nodeOrLeafName == null,*/
                // 名前が見つかった場合
                () -> {
                    System.out.println("Node/Leaf Clicked: " + nodeOrLeafName.get());
                },
                // 名前が見つからなかった場合
                () -> {
                    System.out.println("No Node/Leaf found at this location.");
                }
            );
        });
        return;
    }

    /**
	 * マウスカーサの形状を移動の形に変化させ、指定されたマウスイベントからマウスカーサの位置を獲得して、
	 * インスタンス変数currentに設定すると共に、以前のマウスカーサの位置からの差分を計算する。
	 * そして、その差分だけビューに対してスクロールを依頼し、その後にビューの再描画を依頼する。
	 * 最後にインスタンス変数previousをインスタンス変数currentに更新する。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mouseDragged(MouseEvent aMouseEvent)
	{
		Cursor aCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		Component aComponent = (Component)aMouseEvent.getSource();
		aComponent.setCursor(aCursor);
		this.current = aMouseEvent.getPoint();
		Integer x = this.current.x - this.previous.x;
		Integer y = this.current.y - this.previous.y;
		Point aPoint = new Point(x, y);
		//this.scrollBy(aPoint, aMouseEvent);
		view.scrollBy(aPoint);
		this.previous = this.current;
		return;
	}

    /**
	 * 何もしない。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mouseEntered(MouseEvent aMouseEvent)
	{
		return;
	}

    /**
	 * 何もしない。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mouseExited(MouseEvent aMouseEvent)
	{
		return;
	}

    /**
	 * 何もしない。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mouseMoved(MouseEvent aMouseEvent)
	{
		return;
	}

    /**
	 * マウスカーサの形状を十字に変化させ、指定されたマウスイベントからマウスカーサの位置を獲得して、
	 * インスタンス変数currentに設定する共にインスタンス変数previousをインスタンス変数currentに更新する。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mousePressed(MouseEvent aMouseEvent)
	{
		Cursor aCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		Component aComponent = (Component)aMouseEvent.getSource();
		aComponent.setCursor(aCursor);
		this.current = aMouseEvent.getPoint();
		this.previous = this.current;
		return;
	}

    /**
	 * マウスカーサの形状をデフォルトに戻し、指定されたマウスイベントからマウスカーサの位置を獲得して、
	 * インスタンス変数currentに設定する共にインスタンス変数previousをインスタンス変数currentに更新する。
	 * @param aMouseEvent マウスイベント
	 */
	@Override
	public void mouseReleased(MouseEvent aMouseEvent)
	{
		Cursor aCursor = Cursor.getDefaultCursor();
		Component aComponent = (Component)aMouseEvent.getSource();
		aComponent.setCursor(aCursor);
		this.current = aMouseEvent.getPoint();
		this.previous = this.current;
		return;
	}

    /**
	 * マウスホィールで、縦（垂直）方向のスクロールを行う。
	 * その際に、何らかの修飾があれば、横（水平）方向のスクロールも行う。
	 * @param aMouseWheelEvent マウスホィールイベント
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent aMouseWheelEvent)
	{
		Integer scrollAmount = -(aMouseWheelEvent.getWheelRotation());
		try { new Condition(() -> scrollAmount == 0).ifTrue(() -> { throw new RuntimeException(); }); }
		catch (RuntimeException anException) { return; }
		ValueHolder<Point> aPoint = new ValueHolder<Point>(new Point(0, scrollAmount));
		Integer someModifiers = aMouseWheelEvent.getModifiersEx();
		new Condition(() -> someModifiers > 0).ifTrue(() -> { aPoint.set(new Point(scrollAmount, 0)); });
		this.view.scrollBy(aPoint.get());
		this.view.repaint();
		return;
	}

    /**
	 * スクロール量を指定された座標分だけ相対スクロールして、ビューを再描画する。
	 * その際に、シフトキーが押下されていたら、モデルの依存物となっている全てのビューを連動させて、
	 * 相対（それぞれのビューのスクロール量に見合う分の）スクロールを行う。
	 * さらに、シフトキー押下に加えて、
	 * オブション(alt)キーまたはコントロールキーまたはメタ（コマンド⌘）キーも押下されていたならば、
	 * 全ビュー連動スクロールを、絶対（操作対象ビューのスクロール量を全ビューに適用して）スクロールを行う。
	 * @param aPoint X軸とY軸のスクロール量を表す座標
	 * @param aMouseEvent マウスイベントまたはマウスホィールイベント
	 */
	public void scrollBy(Point aPoint, MouseEvent aMouseEvent)
	{
		this.view.scrollBy(aPoint);
		this.view.repaint();
		Integer someModifiers = aMouseEvent.getModifiersEx();
		Boolean shiftDown = (someModifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
		try { new Condition(() -> !shiftDown).ifTrue(() -> { throw new RuntimeException(); }); }
		catch (RuntimeException anException) { return; }
		Point scrollAmount = this.view.scrollAmount();
		Point scrollOffset = new Point(0 - scrollAmount.x, 0 - scrollAmount.y);
		Integer altORctrlORmetaMask = InputEvent.ALT_DOWN_MASK
		                            | InputEvent.CTRL_DOWN_MASK
		                            | InputEvent.META_DOWN_MASK
		                            ;
		Boolean altORctrlORmetaDown = (someModifiers & altORctrlORmetaMask) != 0;
		view.model.dependents.forEach((View aView) ->
		{
			new Condition(() -> aView != this.view).ifTrue(() ->
			{
				new Condition(() -> !altORctrlORmetaDown).ifThenElse(() -> 
				{ aView.scrollBy(aPoint); }, () -> 
				{ aView.scrollTo(scrollOffset); });
				aView.repaint();
			});
		});
		return;
	}

    /**
	 * 指定されたモデルをインスタンス変数modelに設定する。
	 * @param aModel このコントローラのモデル
	 */
	public void setModel(ForestModel aModel)
	{
		this.model = aModel;
		return;
	}

    /**
	 * 指定されたビューをインスタンス変数viewに設定し、
	 * ビューのマウスのリスナおよびモーションリスナそしてホイールリスナをこのコントローラにする。
	 * @param aView このコントローラのビュー
	 */
	public void setView(ForestView aView)
	{
		this.view = aView;
		this.view.addMouseListener(this);
		this.view.addMouseMotionListener(this);
		this.view.addMouseWheelListener(this);
		return;
	}

    /**
     * 自分自身を文字列に変換するメソッドです。
     */
    @Override
    public java.lang.String toString()
    {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[");
        aBuffer.append(this);
        aBuffer.append("]");
        return aBuffer.toString();
    }


}
