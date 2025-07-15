/**
 * このパッケージは、樹状整列アプリケーションで利用される汎用的なユーティリティクラスを提供します。
 * 主に、関数型インターフェース（{@code Supplier}, {@code Runnable}, {@code Function}）を
 * 活用した条件分岐やループ処理の抽象化、および汎用的な値の保持機能が含まれています。
 *
 * <p>主なクラス:</p>
 * <ul>
 * <li>{@link utility.Condition}: 条件分岐と論理演算を関数型スタイルで扱うためのユーティリティを提供します。</li>
 * <li>{@link utility.Loop}: 条件が真である限りブロックを実行する 'while' ループのラッパーを提供します。</li>
 * <li>{@link utility.ValueHolder}: ジェネリクスを活用し、任意の型の単一の値を保持するクラスです。</li>
 * </ul>
 */
package utility;