import org.atilika.kuromoji.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 形態素解析の結果から、名詞のTF-IDF値を導出するクラスです。
 * Created by Koya Kimura on 2014/04/29.
 */
public class MoTfIdf{

    /**
     * 単語とそのTF-IDF値をまとめていろいろするためのクラスです。
     * なぜ作ったのか：そのほうが便利そうだから。
     */
    public class TermAndTfIdf {
        private String term;
        private double tf;
        private double idf;
        private double tfIdf;

        public String getTerm() {
            return term;
        }

        private void setTerm(String term) {
            this.term = term;
        }

        public double getTf() {
            return tf;
        }

        private void setTf(double tf) {
            this.tf = tf;
        }

        public double getIdf() {
            return idf;
        }

        private void setIdf(double idf) {
            this.idf = idf;
        }

        public double getTfIdf() {
            return tfIdf;
        }

        private void setTfIdf(double tfIdf) {
            this.tfIdf = tfIdf;
        }
    }

    /**
     * 全文書
     */
    private List<List<Token>> tokensList;

    /**
     * 調べる文書
     */
    private List<Token> targetToken;

    /**
     * 全文書から名詞以外を除いたもの
     */
    private List<List<Token>> tokensListNoun;

    /**
     * 調べる文書
     */
    private List<Token> targetTokenNoun;

    /**
     * 調べる文書から名詞以外の単語と単語の重複を除いたもの。
     * なぜSetを使うか：これにadd()するだけで自動で重複が排除されるから。
     */
    private Set<Token> targetTokenSetNoun;

    /**
     * コンストラクタ
     * @param tokensList 文書のリスト（文書集合）
     * @param targetToken TF-IDF値を導出する文書
     */
    MoTfIdf(List<List<Token>> tokensList, List<Token> targetToken) {
        this.tokensList = tokensList;
        this.targetToken = targetToken;
        targetTokenToOnlyNoun();
        targetTokenNounToSet();
        tokensListToOnlyNoun();
    }

    MoTfIdf() {
        System.err.println("コンストラクタを指定してください。");
    }

    /**
     * 調べる文書から重複を除く。
     * なぜprivateか：このクラスでしか使わないから。
     */
    private void targetTokenNounToSet() {
        this.targetTokenSetNoun = new HashSet<Token>();
        List<String> targetTokenNounString = new ArrayList<String>();
        for (Token tmp : this.targetTokenNoun) {
            if (!targetTokenNounString.contains(tmp.getSurfaceForm())) {
                this.targetTokenSetNoun.add(tmp);
            }
            targetTokenNounString.add(tmp.getSurfaceForm());
        }
    }

    /**
     * 全文書から名詞以外を除く
     */
    private void tokensListToOnlyNoun() {
        this.tokensListNoun = new ArrayList<List<Token>>();
        for (List<Token> tmpToken : this.tokensList) {
            List<Token> tokenNoun = new ArrayList<Token>();
            for (Token tmp : tmpToken) {
                if (tmp.getAllFeaturesArray()[0].equals("名詞")) {
                    tokenNoun.add(tmp);
                }
            }
            tokensListNoun.add(tokenNoun);
        }
    }

    /**
     * 調べる文書から名詞以外を除く
     */
    private void targetTokenToOnlyNoun() {
        this.targetTokenNoun = new ArrayList<Token>();
        for (Token tmp : this.targetToken) {
            if (tmp.getAllFeaturesArray()[0].equals("名詞")) {
                this.targetTokenNoun.add(tmp);
            }
        }
    }

    /**
     * インスタンス生成時に指定した文書リストと注目する文書からTF-IDF値を計算します。
     * なぜprotectedか：文書中から特徴のある単語を調べるのがTF-IDFで1つの単語だけ調べても仕方ない。
     * なので外では使ってほしくないという思い。もうひとつのcalc()用。ただ、サブクラスからは参照したい。
     *
     * @param term TF-IDF値を求めたい単語
     * @return 単語のTF-IDF値
     */
    protected double[] calc(Token term) {

        /**
         * ある単語t(Term)の文書d(Document)内での出現回数
         */
        double numberOfTermInDocument;

        /**
         * 文書d(Document)内のすべての単語の出現回数の和
         */
        double totalOfAllTermInDocument;

        /**
         * 文書d(Document)内のある単語t(Term)のTF(Term Frequency)値
         */
        double tf;

        /**
         * ある単語t(Term)のIDF(Inverse Document Frequency)値
         */
        double idf;

        /**
         * 全文書数
         */
        double numberOfAllDocument;

        /**
         * ある単語t(Term)が出現する文書の数
         */
        double df;

        /**
         * TFとIDFを掛けたもの
         * これが文書中の単語の重み
         */
        double tfIdf;

        numberOfTermInDocument = 0;
        for (Token tmpTerm : this.targetTokenNoun) {
            if (tmpTerm.getSurfaceForm().equals(term.getSurfaceForm())) {
                numberOfTermInDocument++;
            }
        }

        System.out.println("**PrintDebug**************************");
        System.out.println("Term: " + term.getSurfaceForm());

        totalOfAllTermInDocument = this.targetTokenNoun.size();

        tf = numberOfTermInDocument / totalOfAllTermInDocument;
        System.out.println("NumberOfTermInDocument(n): " + numberOfTermInDocument);
        System.out.println("TotalOfAllTermInDocument(SigmaN): " + totalOfAllTermInDocument);
        System.out.println("TF: " + tf);

        numberOfAllDocument = this.tokensListNoun.size();

        df = 0;
        for (List<Token> tmpDoc : this.tokensListNoun) {
            for (Token tmpTerm : tmpDoc) {
                if (tmpTerm.getSurfaceForm().equals(term.getSurfaceForm())) {
                    df++;
                    break;
                }
            }
        }

        idf = Math.log(numberOfAllDocument / df) / Math.log(2.0);

        tfIdf = tf * idf;

        System.out.println("NumberOfAllDocument: " + numberOfAllDocument);
        System.out.println("DF: " + df);
        System.out.println("IDF: " + idf);
        System.out.println("TF-IDF: " + tfIdf);

        double[] result = {tf, idf, tfIdf};
        return result;
    }


    List<TermAndTfIdf> calc() {
        List<TermAndTfIdf> result = new ArrayList<TermAndTfIdf>();

        for (Token targetTerm : this.targetTokenSetNoun) {
            TermAndTfIdf tmpTermAndTfIdf = new TermAndTfIdf();
            tmpTermAndTfIdf.setTerm(targetTerm.getSurfaceForm());
            double[] tfIdfTfIdf = calc(targetTerm);
            tmpTermAndTfIdf.setTf(tfIdfTfIdf[0]);
            tmpTermAndTfIdf.setIdf(tfIdfTfIdf[1]);
            tmpTermAndTfIdf.setTfIdf(tfIdfTfIdf[2]);
            result.add(tmpTermAndTfIdf);
        }
        return result;

    }


}
