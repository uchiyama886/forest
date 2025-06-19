package utility;

import java.util.function.Function;
import java.util.function.Consumer;

/**
 * ジェネリクスを活用する例題クラス:形式型<V>を用いて、ある値を保持する
 */
public class ValueHolder<V> extends Object
{
    // 形式型<V>という「formal type parameter」で値を保持するフィールドを宣言する
    private V value;
    /**
     * コンストラクタ
     */
    public ValueHolder()
    {
        this.value = null;
        return;
    }

    /**
     * 受け取った値で初期化するコンストラクタ
     * @param initialValue
     */
    public ValueHolder(V initialValue)
    {
        this.value = initialValue;
        return;
    }

    /**
     * 保持してオブジェクトの値
     * @return 保持している形式型<V>の値
     */
    public V get()
    {
        return this.value;
    }
    /**
     * 引数で渡された値を保持する
     * @param anObject 保持される形式型<V>の値
     * @return 以前に保持していた形式型<V>の値
     */
    public V set(V anObject)
    {
        V previousValue = this.value;
        this.value = anObject;
        return previousValue;
    }

    /**
     * 保持している値を指定された関数で計算した値に更新する。
     * @param aFunction 現在の値を引数にとり、新しい値を返す関数
     */
    public void setDo(Function<V, V> aFunction) {
        this.value = aFunction.apply(this.value);
    }

    /**
     * 自分自身を文字列にして応答する
     * @return このオブジェクトを表す文字列
     */
    @Override
    public String toString()
    {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[");
        aBuffer.append(this.value);
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
