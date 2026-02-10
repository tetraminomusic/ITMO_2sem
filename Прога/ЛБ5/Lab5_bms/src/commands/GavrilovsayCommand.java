package commands;

import managers.CollectionManager;

public class GavrilovsayCommand implements Command {
    private final CollectionManager collectionManager;

    private final String Gavrilov_art = """
            sbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbG                                                   u             \s
            bMMMMMDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKMMMAx eghiiiiiifglifg7WbN y T                           HAO8jhigegdjh\s
            bMMDAAAt                                    LHAAAAAugjhiiiijjjjdhbTV hAobm A  yZ                        AASijhijfki\s
            bMAAA                                              gBAYiiifgggnto8z    y                                  ALb0eeimc\s
            bAA                                                   AMijghhjp6O2AANA  o gj                               AQchdeem\s
            bAj                                                    AKfgkgk5PEKG2   EAAAYkZk9OUHUUAABCEJJJU5ct           A1jgdgh\s
            bA                                                      AUjjgi4n      MAKM7ddhe3784OZY9fdc0da7YYUWn     z   wA6jfie\s
            bA                                                       Agkggogj    wA5a8ZY43goobf9hhahacd5T0de85V          N809kf\s
            bA                                                       AgghfW n    RMNOEE25dgbhgmljsmmihmnjhijf89hx        AWn0hf\s
            bA                                                       AkhhhZ S t  MIAAP3ea2d9dgba97h0khhfdjgig9ikx        APg8gi\s
            bA      %m       AggggbL H9  AALdidf9c9ab589b0931V1XLKKR5em7i         Qgbkf\s
            bA                                                       Jgjjjnf p  6AXy2AAFVY70760d04YMJN4     y777XUQ      3Nkbkk\s
            bA                                                      A6iihhjQM6 5A3xx     qaijgkegiz    bABQ7  umbOA   z  A8hbkh\s
            bA                                                     ATkgfigjmiD4AA7Y  hdu    q6ciw   ys     p0blbn    AVxA1jgghi\s
            bAA                                                  dC7fhhenilhm   R   a    p K    v4bz 8 vn8mqg15 v6JW Pdz3gjjgjo\s
            bIA                                                 ADdhndnjmnnlmy AAOAA7j3Tblbv U1S  Z5hib65880kynihhjl Pj 9glmhid\s
            bMAk                                              AAAggdnjlmkjikms5UCAJ9Z36Y1ID4MT89Sug1U76b5ZV5Z89cfiom cYg7nlnffj\s
            bMAA                                                AH4kiklmkonmkp tA8576QNLM9 ZA58jhqr o09agsw njfqnm7s jgmlkjllcg\s
            bMHA                                                  AH1ljomkkjnt XADB0e9hix AAAPAAAAAY uo43125afbjenwqvaihmhijijn\s
            bMMAA                                                   AOjmklskkd OAA7mkjcpcs          aVcpf0a0gjnlckt  isgklmliji\s
            bMMGAAd                                                  AG4lpthph dAOplaaZ6W2au     gpst6UZ556cjchoiqoksV 5upikijj\s
            bMMMMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA                     AVkqgqm uAaszjiw 9 q  et I7       ynmetmimfh  M82rmnmnok\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAAAAA                  ITnogh  Ahjt9     jy8owz  votyu r kieiijipwz ejmqomjhkk\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKAAAAAAAA            PT8hm  A7Hrhe  qadUEAFBBOVZ2Z1f xajzogabtquzirhmnkmnnn\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAAF3AId       kAJew  Sqinqq5645k      wujkhbckkjdckk wppfm nmnolmmn\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAy djdKAAAy    8AX  At zqdjm zjfkjgiegiotmjigltutn   f8 e7elikklkl\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAz dkrpmqh9EAAA   HAA1c sdga3abW1X22ad36bpfbjkljv vf9jW 4 jvnoknlk\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAz jdkqoqstxdr2  AAAAgJo ym6RW9cb545Q59he2lw    qvcimZ  Rdqmechmqq\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAw  kspolbbuA0  AAKGA  AZu    oZY3fedak8x    urd5 zu6u cVc8d 2AAPm\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMAct2AQ7ci      AAHHDAA  AAAV0            rb383hm  u2b  GL8e1G   AH\s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMA     v   AAA  ABIPSAx     64JLDaM97GGJHMZcnru   oOy   COX63HAn   \s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMA      AAAAAA AATAAWcAAN I      eZ86bfdkuq      VA      TAAADAAS  \s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMA    AAA5VAA FAMw  YAF  qA s5n               XIAE  A          3AA \s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMA  AAAE1zAAh  y uVAA       A 5AAN387kplj63SOKBIV   D   aD1s       \s
            bMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMA   qAA AAN  YAAAA      A       3AWZZ72323KDAz   hpZ       eAFRekd\s
            sbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbA      xf                k                                        \s 
                """;
    public GavrilovsayCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String argument) {
        if (argument != null) {
            if (argument.length() < 40) {
                String message = " ".repeat((40 - argument.length()) / 2) + argument + " ".repeat((40 - argument.length()) / 2 + 3);
                System.out.println(Gavrilov_art.replace("%m", message));
            } else {
                System.out.println("Ошибка: слишком длинная фраза (до 40 символов)");
            }
        } else {
            String message = " ".repeat((40 - 33) / 2) + "Бро, тебе надо ввести фразу и тд." + " ".repeat((40 - 33) / 2 + 3);
            System.out.println(Gavrilov_art.replace("%m", message));
        }
    }

    @Override
    public String getDescription() {
        return "описание тут излишне";
    }
}
