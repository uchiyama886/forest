package forest;

import java.io.*;
import java.util.ArrayList;

import java.awt.image.BufferedImage;

public class Model {
    /**
	 * 依存物（Observerデザインパターンの観測者）：Viewのインスタンスたちを束縛する。
	 */
	protected ArrayList<View> dependents;

    /**
     * 樹状整列それ自身を記憶しておく
     */
    private Forest forest;

	/**
	 * 内容物として画像を束縛する。
	 */
	private BufferedImage picture;

	/**
	 * インスタンスを生成して初期化して応答する。
	 */
	public Model()
	{

	}

	/**
	 * 指定されたビューを依存物に設定する。
	 * @param aView このモデルの依存物となるビュー
	 */
	public void addDependent(View aView)
	{
		this.dependents.add(aView);
		return;
	}

    /**
     * 
     */
    public void animate() {

    }

    /**
     * 
     */
    public void arrange() {

    }

	/**
	 * モデルの内部状態が変化していたので、自分の依存物へupdateのメッセージを送信する。
	 */
	public void changed()
	{
		this.dependents.forEach((View aView) -> { aView.update(); });
		return;
	}

    /**
     * 
     */
    public Forest forest() {

    }

	/**
	 * 画像（モデルの内容物）を応答する。
	 * @return このモデルのpictureフィールドに格納されている画像
	 */
	public BufferedImage picture()
	{
		return this.picture;
	}

    /**
     * 
     */
    protected void read(File aFile) {

    }

    public Node root() {

    }

    public ArrayList<Node> roots() {

    }

	/**
	 * このインスタンスを文字列にして応答する。
	 * @return 自分自身を表す文字列
	 */
	@Override
	public String toString()
	{
		StringBuffer aBuffer = new StringBuffer();
		Class<?> aClass = this.getClass();
		aBuffer.append(aClass.getName());
		aBuffer.append("[picture=");
		aBuffer.append(this.picture);
		aBuffer.append("]");
		return aBuffer.toString();
	}
}

