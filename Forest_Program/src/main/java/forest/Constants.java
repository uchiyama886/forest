package forest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

/**
 * 定数たち：すべてパブリック＆スタティック＆ファイナルの宣言で、
 * このクラスのクラス変数（フィールド）としてアクセス（読み取りが）できます。 
 * 樹状整列で用いるリテラル（定数）項の散在を防止するために、このクラスにまとめています。
 */
public class Constants extends Object
{
    /**
     * 樹状整列データファイル中のタグ「ツリー」を表します。
     */
    public static final String TagofTrees;

    /**
     * 樹状整列データファイル中のタグ「ノード」を表します。
     */
    public static final String TagOfBranches;

    /**
     * ノードを描く際のラベルの文字色を表します。
     */
    public static final Color ForegroundColor;

    /**
     * ノードを描く際のラベルの背景色を表します。
     */
    public static final Color BackgroundColor;

    /**
     * ノードを描く際のラベルのフォントを表します。
     */
    public static final Font DefaultFont;

    /**
     * ノードにおいてラベルを描く際の枠縁から余裕（マージン）を表します。
     */
    public static final Point Margin;

    /**
     * ノード群を樹状に整列させる際にノード同士の間隔を表します。
     */
    public static final Point Interval;

    /**
     * ノード群を深さ優先にたどる際の状態「未定」を表します。
     */
    public static final Integer UnKnown;

    /**
     * ノード群を深さ優先にたどる際の状態「未訪問」を表します。
     */
    public static final Integer UnVisited;

    /**
     * ノード群を深さ優先にたどる際の状態「訪問済」を表します。
     */
    public static final Integer Visited;

    /**
     * 樹状整列アニメーションのチックタック：時間間隔：スピードを表します。
     */

    public static final Integer SleepTick;

    public Constants(){}

} 