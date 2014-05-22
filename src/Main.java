import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koya Kimura on 2014/04/28.
 */
public class Main {
    public static void main(String[] args) {

        // このトークナイザーで解析する
        Tokenizer tokenizer = Tokenizer.builder().build();

        // トークンに分割
        List<Token> tokens1 = tokenizer.tokenize("アイカツは最高のアニメです。アイカツはカードゲームもあります。");
        List<Token> tokens2 = tokenizer.tokenize("アイカツはアイドルアニメだ。プリティーリズムもそう。");
        List<Token> tokens3 = tokenizer.tokenize("アイカツはプリキュアの売り上げを超えました。");
        List<Token> tokens4 = tokenizer.tokenize("アイカツは今冬、映画が公開されます。");
        List<Token> tokens5 = tokenizer.tokenize("アイカツのアニメはもうすぐ100話を迎えます。");
        List<Token> tokens6 = tokenizer.tokenize("アイカツのコアターゲットは小学1年生から小学3年生の女児です。");

        // 文書のリスト（文書集合）を生成
        List<List<Token>> tokensList = new ArrayList<List<Token>>();
        tokensList.add(tokens1);
        tokensList.add(tokens2);
        tokensList.add(tokens3);
        tokensList.add(tokens4);
        tokensList.add(tokens5);
        tokensList.add(tokens6);

        // インスタンス生成
        MoTfIdf mti = new MoTfIdf(tokensList, tokens3);

        // TF-IDF値の導出
        List<MoTfIdf.TermAndTfIdf> tati = mti.calc();

        // 表示
        for(MoTfIdf.TermAndTfIdf tmp : tati) {
            System.out.println("****************************");
            System.out.println("Term: " + tmp.getTerm());
            System.out.println("TF: " + tmp.getTf());
            System.out.println("IDF: " + tmp.getIdf());
            System.out.println("TF-IDF: " + tmp.getTfIdf());
        }
    }
}