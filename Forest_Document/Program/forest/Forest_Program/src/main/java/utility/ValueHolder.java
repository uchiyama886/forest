package utility;

import java.util.function.Function;
//import java.util.function.Consumer;

/**
 * ジェネリクスを活用する例題クラス: ある値を保持します。
 * クラス宣言の {@code <V>} は「形式型パラメータ」と呼ばれ、
 * {@code ValueHolder} クラスが任意の型の値を保持できることを示します。
 * 例えば、{@code ValueHolder<String>} は文字列を、{@code ValueHolder<Integer>} は整数を保持できます。
 * @param <V> このValueHolderが保持する値の型。
 */
public class ValueHolder<V> extends Object
{
    /**
     * 形式型 {@code <V>} で宣言された、この {@code ValueHolder} が保持する値のフィールド。
     * このフィールドの型は、{@code ValueHolder} オブジェクトが生成される際に指定される型によって決定されます。
     */
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
     * @param initialValue この {@code ValueHolder} に初期値として設定される、型 {@code V} の値。
     */
    public ValueHolder(V initialValue)
    {
        this.value = initialValue;
        return;
    }

    /**
     * 保持してオブジェクトの値
     * @return この {@code ValueHolder} が保持している、形式型 {@code <V>} の値。
     */
    public V get()
    {
        return this.value;
    }
    /**
     * 引数で渡された値を保持する
     * @param anObject 新しく保持される、形式型 {@code <V>} の値。
     * @return 値が更新される前に以前に保持していた、形式型 {@code <V>} の値。
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
