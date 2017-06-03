package kr.ac.kaist.team888.util;

import android.os.AsyncTask;
import android.os.Environment;

import kr.ac.kaist.team888.bezier.BezierCurve;
import kr.ac.kaist.team888.hangulcharacter.CharacterLoader;
import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.region.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class FontExporter extends AsyncTask<String, Double, File> {
  private static final String DEFAULT_FILE_PATH = "/MyLittleFont/";
  private static final String IP = "172.30.1.43";
  private static final int PORT = 3000;

  private static final int HANGUL_COMPATIBILITY_JAMO_START = 0x3130;
  private static final int HANGUL_COMPATIBILITY_JAMO_END = 0x0318F;
  private static final int HANGUL_SYLLABLES_START = 0xAC00;
  private static final int HANGUL_SYLLABLES_END = 0xD7AF;

  private String path;
  private String fontname;
  private File fontFile;

  private double curve;
  private double roundness;
  private double weight;
  private double width;
  private double flattening;
  private double arise;
  private double gap;
  private ExportType exportType;

  private ExportCallbacks exportCallbacks;

  public enum ExportType {
    PARTIAL,
    ALL
  }

  /**
   * Class for export font synchronously.
   *
   * @param exportType export type
   * @param fontname name of font
   * @param exportCallbacks call backs on async task
   */
  public FontExporter(ExportType exportType, String fontname, ExportCallbacks exportCallbacks) {
    this.exportType = exportType;
    this.fontname = fontname;
    this.exportCallbacks = exportCallbacks;

    this.curve = FeatureController.getInstance().getCurve();
    this.roundness = FeatureController.getInstance().getRoundness();
    this.weight = FeatureController.getInstance().getWeight();
    this.width = FeatureController.getInstance().getWidth();
    this.flattening = FeatureController.getInstance().getFlattening();
    this.arise = FeatureController.getInstance().getArise();
    this.gap = FeatureController.getInstance().getGap();

    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + DEFAULT_FILE_PATH + fontname + ".ttf";
    fontFile = new File(path);
  }

  private String makeFontData(char letter) {
    Locator locator = new Locator(letter, false);
    locator.manipulateSkeleton(flattening, arise);
    locator.applyContour(weight, roundness);
    locator.applyCurve(curve);
    locator.applyWidth(width);

    double regionWidth = locator.locatorRegion.getMaxX()
        - locator.locatorRegion.getMinX();
    double regionGap = regionWidth * gap;
    Region region = new Region(0,
        regionWidth + regionGap,
        locator.locatorRegion.getMinY(),
        locator.locatorRegion.getMaxY());

    String data = letter + "; ";
    data += (regionWidth + regionGap) + "; ";

    ArrayList<ArrayList<BezierCurve>> contour = locator.getContour();
    for (ArrayList<BezierCurve> stroke : contour) {
      for (int pointIndex = 0; pointIndex < stroke.size(); pointIndex++) {
        BezierCurve curve = locator.locatorRegion.transformBezierCurve(
            region, stroke.get(pointIndex));

        if (pointIndex == 0) {
          data += String.format("%.2f ", curve.getStartPoint().getX());
          data += String.format("%.2f ", curve.getStartPoint().getY());
          data += ", ";
        }

        data += String.format("%.2f ", curve.getEndPoint().getX());
        data += String.format("%.2f ", curve.getEndPoint().getY());
        if (curve.getOrder() > 1) {
          data += String.format("%.2f ", curve.getControlPoints()[0].getX());
          data += String.format("%.2f ", curve.getControlPoints()[0].getY());
        }
        data += ", ";

        if (pointIndex == stroke.size() - 1) {
          data += String.format("%.2f ", curve.getEndPoint().getX());
          data += String.format("%.2f ", curve.getEndPoint().getY());
          data += "; ";
        }
      }
    }
    return data;
  }

  @Override
  protected void onProgressUpdate(Double... values) {
    super.onProgressUpdate(values);
    if (exportCallbacks != null) {
      exportCallbacks.onProgress(values[0]);
    }
  }

  @Override
  protected void onPostExecute(File file) {
    super.onPostExecute(file);
    if (exportCallbacks != null) {
      exportCallbacks.onEnd(file);
    }
  }

  @Override
  protected File doInBackground(String... params) {
    Socket socket;
    try {
      socket = new Socket(IP, PORT);
      BufferedWriter networkWriter = new BufferedWriter(
          new OutputStreamWriter(socket.getOutputStream()));
      PrintWriter out = new PrintWriter(networkWriter, true);

      out.println("$$" + fontname);
      // sample of pushing data to server
//      out.println("가 ; 1040 ; 256 0, 512 512 512 0, 256 1024 512 1024, 0 512 0 1024, 256 0 0" +
//          "0 ; 768 0, 1024 512 1024 0, 768 1024 1024 1024, 512 512 512 1024, 768 0 512 0");

      int totalLength = HANGUL_COMPATIBILITY_JAMO_END - HANGUL_COMPATIBILITY_JAMO_START;
      int progressCount = 0;
      switch (exportType) {
        case ALL:
          // send UNICODE Syllables data
          totalLength += HANGUL_SYLLABLES_END - HANGUL_SYLLABLES_START;
          for (int i = HANGUL_SYLLABLES_START; i < HANGUL_SYLLABLES_END; i++) {
            progressCount++;
            if (CharacterLoader.getInstance().isDrawable((char)i)) {
              out.println(makeFontData((char)i));
            }
            publishProgress(0.9 * progressCount / totalLength);
          }
          break;
        case PARTIAL:
          // send KS5601 Sysllables data
          totalLength += KS5601.length;
          for (char letter : KS5601) {
            progressCount++;
            if (CharacterLoader.getInstance().isDrawable(letter)) {
              out.println(makeFontData(letter));
            }
            publishProgress(0.9 * progressCount / totalLength);
          }
          break;
        default:
          break;
      }

      // send Compatibility jamo data
      for (int i = HANGUL_COMPATIBILITY_JAMO_START; i < HANGUL_COMPATIBILITY_JAMO_END; i++) {
        progressCount++;
        if (CharacterLoader.getInstance().isDrawable((char)i)) {
          out.println(makeFontData((char)i));
        }
        publishProgress(0.9 * progressCount / totalLength);
      }

      out.println("$$");

      byte[] bytes = new byte[1024];

      InputStream inputStream = socket.getInputStream();
      inputStream.read(bytes, 0, 8);
      ByteBuffer buffer = ByteBuffer.allocate(8);
      buffer.put(bytes, 0, 8);
      buffer.flip();
      long fileLength = buffer.getLong();

      FileOutputStream fileOutputStream = new FileOutputStream(fontFile);
      int readbyte;
      int totalReadByte = 0;

      while ((readbyte = inputStream.read(bytes)) > 0) {
        totalReadByte += readbyte;
        publishProgress(0.9 + 0.1 * (totalReadByte / (double)fileLength));
        fileOutputStream.write(bytes, 0, readbyte);
      }

      fileOutputStream.close();
      inputStream.close();
      socket.close();
      return fontFile;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Interface for getting result of exporting progress.
   */
  public interface ExportCallbacks {
    /**
     * Callback during exporting.
     *
     * @param value progress ahead of exporting. Range 0~1
     */
    void onProgress(double value);

    /**
     * Callback after exporing finished.
     *
     * @param file result file. return null on export fail.
     */
    void onEnd(File file);
  }

  private static final char[] KS5601 = ("가각간갇갈갉갊감갑값갓갔강갖갗같갚갛개객갠갤갬갭갯갰갱갸갹갼걀걋걍걔걘걜거"
      + "걱건걷걸걺검겁것겄겅겆겉겊겋게겐겔겜겝겟겠겡겨격겪견겯결겸겹겻겼경곁계곈곌곕곗고곡곤곧골곪곬곯곰곱곳공곶과곽관괄괆괌괍괏"
      + "광괘괜괠괩괬괭괴괵괸괼굄굅굇굉교굔굘굡굣구국군굳굴굵굶굻굼굽굿궁궂궈궉권궐궜궝궤궷귀귁귄귈귐귑귓규균귤그극근귿글긁금급긋"
      + "긍긔기긱긴긷길긺김깁깃깅깆깊까깍깎깐깔깖깜깝깟깠깡깥깨깩깬깰깸깹깻깼깽꺄꺅꺌꺼꺽꺾껀껄껌껍껏껐껑께껙껜껨껫껭껴껸껼꼇꼈꼍"
      + "꼐꼬꼭꼰꼲꼴꼼꼽꼿꽁꽂꽃꽈꽉꽐꽜꽝꽤꽥꽹꾀꾄꾈꾐꾑꾕꾜꾸꾹꾼꿀꿇꿈꿉꿋꿍꿎꿔꿜꿨꿩꿰꿱꿴꿸뀀뀁뀄뀌뀐뀔뀜뀝뀨끄끅끈끊끌끎"
      + "끓끔끕끗끙끝끼끽낀낄낌낍낏낑나낙낚난낟날낡낢남납낫났낭낮낯낱낳내낵낸낼냄냅냇냈냉냐냑냔냘냠냥너넉넋넌널넒넓넘넙넛넜넝넣네"
      + "넥넨넬넴넵넷넸넹녀녁년녈념녑녔녕녘녜녠노녹논놀놂놈놉놋농높놓놔놘놜놨뇌뇐뇔뇜뇝뇟뇨뇩뇬뇰뇹뇻뇽누눅눈눋눌눔눕눗눙눠눴눼뉘"
      + "뉜뉠뉨뉩뉴뉵뉼늄늅늉느늑는늘늙늚늠늡늣능늦늪늬늰늴니닉닌닐닒님닙닛닝닢다닥닦단닫달닭닮닯닳담답닷닸당닺닻닿대댁댄댈댐댑댓"
      + "댔댕댜더덕덖던덛덜덞덟덤덥덧덩덫덮데덱덴델뎀뎁뎃뎄뎅뎌뎐뎔뎠뎡뎨뎬도독돈돋돌돎돐돔돕돗동돛돝돠돤돨돼됐되된될됨됩됫됴두둑"
      + "둔둘둠둡둣둥둬뒀뒈뒝뒤뒨뒬뒵뒷뒹듀듄듈듐듕드득든듣들듦듬듭듯등듸디딕딘딛딜딤딥딧딨딩딪따딱딴딸땀땁땃땄땅땋때땍땐땔땜땝땟"
      + "땠땡떠떡떤떨떪떫떰떱떳떴떵떻떼떽뗀뗄뗌뗍뗏뗐뗑뗘뗬또똑똔똘똥똬똴뙈뙤뙨뚜뚝뚠뚤뚫뚬뚱뛔뛰뛴뛸뜀뜁뜅뜨뜩뜬뜯뜰뜸뜹뜻띄띈띌"
      + "띔띕띠띤띨띰띱띳띵라락란랄람랍랏랐랑랒랖랗래랙랜랠램랩랫랬랭랴략랸럇량러럭런럴럼럽럿렀렁렇레렉렌렐렘렙렛렝려력련렬렴렵렷"
      + "렸령례롄롑롓로록론롤롬롭롯롱롸롼뢍뢨뢰뢴뢸룀룁룃룅료룐룔룝룟룡루룩룬룰룸룹룻룽뤄뤘뤠뤼뤽륀륄륌륏륑류륙륜률륨륩륫륭르륵른"
      + "를름릅릇릉릊릍릎리릭린릴림립릿링마막만많맏말맑맒맘맙맛망맞맡맣매맥맨맬맴맵맷맸맹맺먀먁먈먕머먹먼멀멂멈멉멋멍멎멓메멕멘멜"
      + "멤멥멧멨멩며멱면멸몃몄명몇몌모목몫몬몰몲몸몹못몽뫄뫈뫘뫙뫼묀묄묍묏묑묘묜묠묩묫무묵묶문묻물묽묾뭄뭅뭇뭉뭍뭏뭐뭔뭘뭡뭣뭬뮈"
      + "뮌뮐뮤뮨뮬뮴뮷므믄믈믐믓미믹민믿밀밂밈밉밋밌밍및밑바박밖밗반받발밝밞밟밤밥밧방밭배백밴밸뱀뱁뱃뱄뱅뱉뱌뱍뱐뱝버벅번벋벌벎"
      + "범법벗벙벚베벡벤벧벨벰벱벳벴벵벼벽변별볍볏볐병볕볘볜보복볶본볼봄봅봇봉봐봔봤봬뵀뵈뵉뵌뵐뵘뵙뵤뵨부북분붇불붉붊붐붑붓붕붙"
      + "붚붜붤붰붸뷔뷕뷘뷜뷩뷰뷴뷸븀븃븅브븍븐블븜븝븟비빅빈빌빎빔빕빗빙빚빛빠빡빤빨빪빰빱빳빴빵빻빼빽뺀뺄뺌뺍뺏뺐뺑뺘뺙뺨뻐뻑뻔"
      + "뻗뻘뻠뻣뻤뻥뻬뼁뼈뼉뼘뼙뼛뼜뼝뽀뽁뽄뽈뽐뽑뽕뾔뾰뿅뿌뿍뿐뿔뿜뿟뿡쀼쁑쁘쁜쁠쁨쁩삐삑삔삘삠삡삣삥사삭삯산삳살삵삶삼삽삿샀상"
      + "샅새색샌샐샘샙샛샜생샤샥샨샬샴샵샷샹섀섄섈섐섕서석섞섟선섣설섦섧섬섭섯섰성섶세섹센셀셈셉셋셌셍셔셕션셜셤셥셧셨셩셰셴셸솅"
      + "소속솎손솔솖솜솝솟송솥솨솩솬솰솽쇄쇈쇌쇔쇗쇘쇠쇤쇨쇰쇱쇳쇼쇽숀숄숌숍숏숑수숙순숟술숨숩숫숭숯숱숲숴쉈쉐쉑쉔쉘쉠쉥쉬쉭쉰쉴"
      + "쉼쉽쉿슁슈슉슐슘슛슝스슥슨슬슭슴습슷승시식신싣실싫심십싯싱싶싸싹싻싼쌀쌈쌉쌌쌍쌓쌔쌕쌘쌜쌤쌥쌨쌩썅써썩썬썰썲썸썹썼썽쎄쎈"
      + "쎌쏀쏘쏙쏜쏟쏠쏢쏨쏩쏭쏴쏵쏸쐈쐐쐤쐬쐰쐴쐼쐽쑈쑤쑥쑨쑬쑴쑵쑹쒀쒔쒜쒸쒼쓩쓰쓱쓴쓸쓺쓿씀씁씌씐씔씜씨씩씬씰씸씹씻씽아악안앉"
      + "않알앍앎앓암압앗았앙앝앞애액앤앨앰앱앳앴앵야약얀얄얇얌얍얏양얕얗얘얜얠얩어억언얹얻얼얽얾엄업없엇었엉엊엌엎에엑엔엘엠엡엣"
      + "엥여역엮연열엶엷염엽엾엿였영옅옆옇예옌옐옘옙옛옜오옥온올옭옮옰옳옴옵옷옹옻와왁완왈왐왑왓왔왕왜왝왠왬왯왱외왹왼욀욈욉욋욍"
      + "요욕욘욜욤욥욧용우욱운울욹욺움웁웃웅워웍원월웜웝웠웡웨웩웬웰웸웹웽위윅윈윌윔윕윗윙유육윤율윰윱윳융윷으윽은을읊음읍읏응읒"
      + "읓읔읕읖읗의읜읠읨읫이익인일읽읾잃임입잇있잉잊잎자작잔잖잗잘잚잠잡잣잤장잦재잭잰잴잼잽잿쟀쟁쟈쟉쟌쟎쟐쟘쟝쟤쟨쟬저적전절"
      + "젊점접젓정젖제젝젠젤젬젭젯젱져젼졀졈졉졌졍졔조족존졸졺좀좁좃종좆좇좋좌좍좔좝좟좡좨좼좽죄죈죌죔죕죗죙죠죡죤죵주죽준줄줅줆"
      + "줌줍줏중줘줬줴쥐쥑쥔쥘쥠쥡쥣쥬쥰쥴쥼즈즉즌즐즘즙즛증지직진짇질짊짐집짓징짖짙짚짜짝짠짢짤짧짬짭짯짰짱째짹짼쨀쨈쨉쨋쨌쨍쨔"
      + "쨘쨩쩌쩍쩐쩔쩜쩝쩟쩠쩡쩨쩽쪄쪘쪼쪽쫀쫄쫌쫍쫏쫑쫓쫘쫙쫠쫬쫴쬈쬐쬔쬘쬠쬡쭁쭈쭉쭌쭐쭘쭙쭝쭤쭸쭹쮜쮸쯔쯤쯧쯩찌찍찐찔찜찝찡찢"
      + "찧차착찬찮찰참찹찻찼창찾채책챈챌챔챕챗챘챙챠챤챦챨챰챵처척천철첨첩첫첬청체첵첸첼쳄쳅쳇쳉쳐쳔쳤쳬쳰촁초촉촌촐촘촙촛총촤촨"
      + "촬촹최쵠쵤쵬쵭쵯쵱쵸춈추축춘출춤춥춧충춰췄췌췐취췬췰췸췹췻췽츄츈츌츔츙츠측츤츨츰츱츳층치칙친칟칠칡침칩칫칭카칵칸칼캄캅캇"
      + "캉캐캑캔캘캠캡캣캤캥캬캭컁커컥컨컫컬컴컵컷컸컹케켁켄켈켐켑켓켕켜켠켤켬켭켯켰켱켸코콕콘콜콤콥콧콩콰콱콴콸쾀쾅쾌쾡쾨쾰쿄쿠"
      + "쿡쿤쿨쿰쿱쿳쿵쿼퀀퀄퀑퀘퀭퀴퀵퀸퀼큄큅큇큉큐큔큘큠크큭큰클큼큽킁키킥킨킬킴킵킷킹타탁탄탈탉탐탑탓탔탕태택탠탤탬탭탯탰탱탸"
      + "턍터턱턴털턺텀텁텃텄텅테텍텐텔템텝텟텡텨텬텼톄톈토톡톤톨톰톱톳통톺톼퇀퇘퇴퇸툇툉툐투툭툰툴툼툽툿퉁퉈퉜퉤튀튁튄튈튐튑튕튜"
      + "튠튤튬튱트특튼튿틀틂틈틉틋틔틘틜틤틥티틱틴틸팀팁팃팅파팍팎판팔팖팜팝팟팠팡팥패팩팬팰팸팹팻팼팽퍄퍅퍼퍽펀펄펌펍펏펐펑페펙"
      + "펜펠펨펩펫펭펴편펼폄폅폈평폐폘폡폣포폭폰폴폼폽폿퐁퐈퐝푀푄표푠푤푭푯푸푹푼푿풀풂품풉풋풍풔풩퓌퓐퓔퓜퓟퓨퓬퓰퓸퓻퓽프픈플"
      + "픔픕픗피픽핀필핌핍핏핑하학한할핥함합핫항해핵핸핼햄햅햇했행햐향허헉헌헐헒험헙헛헝헤헥헨헬헴헵헷헹혀혁현혈혐협혓혔형혜혠혤"
      + "혭호혹혼홀홅홈홉홋홍홑화확환활홧황홰홱홴횃횅회획횐횔횝횟횡효횬횰횹횻후훅훈훌훑훔훗훙훠훤훨훰훵훼훽휀휄휑휘휙휜휠휨휩휫휭"
      + "휴휵휸휼흄흇흉흐흑흔흖흗흘흙흠흡흣흥흩희흰흴흼흽힁히힉힌힐힘힙힛힝").toCharArray();
}
