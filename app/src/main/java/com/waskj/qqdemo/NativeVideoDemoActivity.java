package com.waskj.qqdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.nativ.MediaListener;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeMediaAD;
import com.qq.e.ads.nativ.NativeMediaAD.NativeMediaADListener;
import com.qq.e.ads.nativ.NativeMediaADData;
import com.qq.e.union.demo.R;

import java.util.List;

/**
 * 鍘熺敓瑙嗛骞垮憡鎺ュ叆鐨勬櫘閫氬満鏅紝杩欓噷绀轰緥閲岄潰婕旂ず浜嗗浣曚娇鐢ㄥ師鐢熻棰戝箍鍛婇噷闈㈡渶鍩烘湰鐨勬帴鍙ｃ��
 *
 * 鍦ㄥ箍鐐归�歋DK涓紝甯︽湁瑙嗛绱犳潗鐨勫師鐢熷箍鍛婂拰鏅�氬浘鏂囧師鐢熷箍鍛婂彲浠ユ贩鍚堟媺鍙栥�傚綋鎷夊彇澶氭潯骞垮憡鏃讹紝杩斿洖鐨勫垪琛ㄤ腑鍙兘鏃㈡湁瑙嗛绫诲師鐢熷箍鍛婏紝鍙堟湁鏅�氱殑鍥剧墖绫诲師鐢熷箍鍛娿��
 * 锛堟櫘閫氬浘鏂囧師鐢熷箍鍛婃槸2鍥�2鏂囷紝甯﹁棰戠礌鏉愮殑鍘熺敓骞垮憡鏄�2鍥�2鏂�1瑙嗛锛�
 *
 *
 * 涓�鑸媺鍙栧師鐢熻棰戝箍鍛婄殑姝ラ鍒嗕负濡備笅3姝ワ細
 * - 1. loadAD(int adCount)锛氭媺鍙栨垚鍔熷悗浼氬洖璋僶nADLoaded鏂规硶锛屾鏂规硶涓彲浠ヨ幏鍙栧埌骞垮憡瀵硅薄鍒楄〃adList銆�
 * - 2. preLoadVideo()锛氬綋涓�涓箍鍛婂璞′负瑙嗛骞垮憡锛堝嵆ad.isVideoAD()=true锛夋椂锛岄渶瑕佽皟鐢╬reLoadVideo鏂规硶鏉ョ紦瀛樿棰戙�傝棰戠紦瀛樻垚鍔熷悗锛屼細鍥炶皟onADVideoLoaded鏂规硶閫氱煡寮�鍙戣�呫��
 * - 3. bindMediaView(MediaView view, boolean useDefaultController)锛氬綋瑙嗛缂撳瓨瀹屾垚鍚庯紝鍙互缁欒繖涓箍鍛婂璞ind涓�涓狹ediaView缁勪欢锛屽苟璋冪敤play鏂规硶鏉ユ挱鏀捐棰戙��
 *
 *
 * 涓庤棰戠浉鍏崇殑鍩烘湰鎺ュ彛锛�
 * - preLoadVideo()锛氶鍔犺浇瑙嗛绱犳潗
 * - bindView(MediaView view, boolean useDefaultController)锛氱粦瀹歁ediaView缁勪欢锛岄渶鍦ㄨ棰戝姞杞藉畬鎴愬悗璋冪敤
 * - play()锛氭挱鏀�
 * - stop()锛氬仠姝�
 * - resume()锛氭仮澶嶆挱鏀�
 * - destroy()锛氶攢姣佽祫婧�
 *
 */
public class NativeVideoDemoActivity extends Activity {

  private static final String TAG = NativeVideoDemoActivity.class.getSimpleName();
  public static final int AD_COUNT = 1;                 // 涓�娆℃媺鍙栫殑骞垮憡鏉℃暟锛氳寖鍥村拰浠ュ墠鐨勫師鐢熷箍鍛婁竴鏍凤紝鏄�1-10銆備絾鏄垜浠己鐑堝缓璁細闇�瑕佸睍绀哄嚑涓箍鍛婂氨鍔犺浇鍑犱釜锛屼笉瑕佽繃澶氬姞杞藉箍鍛婅�屼笉鍘绘洕鍏夊畠浠�傝繖鏍蜂細瀵艰嚧鏇濆厜鐜囪繃浣庛��

  // 涓庡箍鍛婃棤鍏崇殑鍙橀噺锛屾湰绀轰緥涓殑涓�浜涘叾浠朥I
  private AQuery mAQuery;                               // 鐢ㄤ簬鍔犺浇鍥剧墖AQuery寮�婧愮粍浠讹紝寮�鍙戣�呭彲鏍规嵁闇�姹備娇鐢ㄨ嚜宸辩殑鍥剧墖鍔犺浇妗嗘灦
  private Button mDownloadButton;
  private RelativeLayout mADInfoContainer;
  private TextView textLoadResult;

  // 涓庡箍鍛婃湁鍏崇殑鍙橀噺锛岀敤鏉ユ樉绀哄箍鍛婄礌鏉愮殑UI
  private NativeMediaADData mAD;                        // 鍔犺浇鐨勫師鐢熻棰戝箍鍛婂璞★紝鏈ず渚嬩负绠�渚垮彧婕旂ず鍔犺浇1鏉″箍鍛婄殑绀轰緥
  private NativeMediaAD mADManager;                     // 鍘熺敓骞垮憡manager锛岀敤浜庣鐞嗗箍鍛婃暟鎹殑鍔犺浇锛岀洃鍚箍鍛婂洖璋�
  private MediaView mMediaView;                         // 骞跨偣閫氳棰戝鍣紝闇�瑕佸紑鍙戣�呮坊鍔犲埌鑷繁鐨剎ml甯冨眬鏂囦欢涓紝鐢ㄤ簬鎾斁瑙嗛
  private ImageView mImagePoster;                       // 骞垮憡澶у浘锛屾病鏈夊姞杞藉ソ瑙嗛绱犳潗鍓嶏紝鍏堟樉绀哄箍鍛婄殑澶у浘
  private final Handler tikTokHandler = new Handler();  // 鍊掕鏃惰鍙朒andler锛屽紑鍙戣�呭彲浠ユ寜鐓ц嚜宸辩殑璁捐瀹炵幇锛屾澶勪粎渚涘弬鑰�
  private TextView mTextCountDown;                      // 鍊掕鏃舵樉绀虹殑TextView锛屽箍鐐归�氬師鐢熻棰戝箍鍛奡DK鎻愪緵浜嗛潪甯稿鐨勬帴鍙ｏ紝鍏佽寮�鍙戣�呰嚜鐢卞湴瀹氬埗瑙嗛鎾斁鍣ㄦ帶鍒舵潯鍜屽�掕鏃剁瓑UI

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_video_demo);
    initView();
    initNativeVideoAD();
    loadAD();
  }

  private void initView() {
    mMediaView = (MediaView) findViewById(R.id.gdt_media_view);
    mImagePoster = (ImageView) findViewById(R.id.img_poster);
    mADInfoContainer = (RelativeLayout) this.findViewById(R.id.ad_info_container);
    mDownloadButton = (Button) mADInfoContainer.findViewById(R.id.btn_download);
    mTextCountDown = (TextView) findViewById(R.id.text_count_down);
    textLoadResult = (TextView) findViewById(R.id.text_load_result);
    mAQuery = new AQuery((RelativeLayout) findViewById(R.id.root));
  }

  private void initNativeVideoAD() {
    NativeMediaADListener listener = new NativeMediaADListener() {

      /**
       * 骞垮憡鍔犺浇鎴愬姛
       *
       * @param adList  骞垮憡瀵硅薄鍒楄〃
       */
      @Override
      public void onADLoaded(List<NativeMediaADData> adList) {
        if (adList.size() > 0) {
          mAD = adList.get(0);
          /**
           * 鍔犺浇骞垮憡鎴愬姛锛屽紑濮嬫覆鏌撱��
           */
          initADUI();

          if (mAD.isVideoAD()) {
            /**
             * 濡傛灉璇ユ潯鍘熺敓骞垮憡鏄竴鏉″甫鏈夎棰戠礌鏉愮殑骞垮憡锛岃繕闇�瑕佸厛璋冪敤preLoadVideo鎺ュ彛鏉ュ姞杞借棰戠礌鏉愶細
             *    - 鍔犺浇鎴愬姛锛氬洖璋僋ativeMediaADListener.onADVideoLoaded(NativeMediaADData adData)鏂规硶
             *    - 鍔犺浇澶辫触锛氬洖璋僋ativeMediaADListener.onADError(NativeMediaADData adData, int errorCode)鏂规硶锛岄敊璇爜涓�700
             */
            mAD.preLoadVideo();
            textLoadResult.setText("isVideoAD()=true锛�" + "杩欐槸涓�鏉¤棰戝箍鍛�");
          } else {
            /**
             * 濡傛灉璇ユ潯鍘熺敓骞垮憡鍙槸涓�涓櫘閫氬浘鏂囩殑骞垮憡锛屼笉甯﹁棰戠礌鏉愶紝閭ｄ箞娓叉煋鏅�氱殑UI鍗冲彲銆�
             */
            textLoadResult.setText("isVideoAD()=false锛�" + "杩欎笉鏄竴鏉¤棰戝箍鍛�");
          }

        }
      }

      /**
       * 骞垮憡鍔犺浇澶辫触
       *
       * @param errorCode   骞垮憡鍔犺浇澶辫触鐨勯敊璇爜锛岄敊璇爜鍚箟璇峰弬鑰冨紑鍙戣�呮枃妗ｇ4绔犮��
       */
      @Override
      public void onNoAD(int errorCode) {
        textLoadResult.setText(String.format("骞垮憡鍔犺浇澶辫触锛岄敊璇爜锛�%d", errorCode));
      }

      /**
       * 骞垮憡鐘舵�佸彂鐢熷彉鍖栵紝瀵逛簬App绫诲箍鍛婅�岃█锛屼笅杞�/瀹夎鐨勭姸鎬佸拰涓嬭浇杩涘害鍙互鍙樺寲銆�
       *
       * @param ad    鐘舵�佸彂鐢熷彉鍖栫殑骞垮憡瀵硅薄
       */
      @Override
      public void onADStatusChanged(NativeMediaADData ad) {
        if (ad != null && ad.equals(mAD)) {
          updateADUI();   // App绫诲箍鍛婂湪涓嬭浇杩囩▼涓紝涓嬭浇杩涘害浼氬彂鐢熷彉鍖栵紝濡傛灉寮�鍙戣�呴渶瑕佽鐢ㄦ埛浜嗚В涓嬭浇杩涘害锛屽彲浠ユ洿鏂癠I銆�
        }
      }

      /**
       * 骞垮憡澶勭悊鍙戠敓閿欒锛屽綋璋冪敤涓�涓箍鍛婂璞＄殑onExposured銆乷nClicked銆乸reLoadVideo鎺ュ彛鏃讹紝濡傛灉鍙戠敓浜嗛敊璇細鍥炶皟姝ゆ帴鍙ｏ紝鍏蜂綋鐨勯敊璇爜鍚箟璇峰弬鑰冨紑鍙戣�呮枃妗ｃ��
       *
       * @param adData    骞垮憡瀵硅薄
       * @param errorCode 閿欒鐮侊紝700琛ㄧず瑙嗛鍔犺浇澶辫触锛�701琛ㄧず瑙嗛鎾斁鏃跺嚭鐜伴敊璇�
       */
      @Override
      public void onADError(NativeMediaADData adData, int errorCode) {
        Log.i(TAG, adData.getTitle() + " onADError: " + errorCode);
      }

      /**
       * 褰撹皟鐢ㄤ竴涓箍鍛婂璞＄殑preLoadVideo鎺ュ彛鏃讹紝瑙嗛绱犳潗鍔犺浇瀹屾垚鍚庯紝浼氬洖璋冩鎺ュ彛锛屽湪姝ゅ洖璋冧腑鍙互缁欏箍鍛婂璞＄粦瀹歁ediaView缁勪欢鎾斁瑙嗛銆�
       *
       * @param adData  瑙嗛绱犳潗鍔犺浇瀹屾垚鐨勫箍鍛婂璞★紝寰堟樉鐒惰繖涓箍鍛婁竴瀹氭槸涓�涓甫鏈夎棰戠礌鏉愮殑骞垮憡锛岄渶瑕佺粰瀹僢indView骞舵挱鏀惧畠
       */
      @Override
      public void onADVideoLoaded(NativeMediaADData adData) {
        Log.i(TAG, adData.getTitle() + " ---> 瑙嗛绱犳潗鍔犺浇瀹屾垚"); // 浠呬粎鏄姞杞借棰戞枃浠跺畬鎴愶紝濡傛灉娌℃湁缁戝畾MediaView瑙嗛浠嶇劧涓嶅彲浠ユ挱鏀�
        bindMediaView();
      }

      /**
       * 骞垮憡鏇濆厜鏃剁殑鍥炶皟
       *
       * 娉ㄦ剰锛氬甫鏈夎棰戠礌鏉愮殑鍘熺敓骞垮憡鍙互澶氭鏇濆厜 鎸夌収鏇濆厜璁¤垂
       * 娌″甫鏈夎棰戠礌鏉愮殑骞垮憡鍙兘鏇濆厜涓�娆� 鎸夌収鐐瑰嚮璁¤垂
       *
       * @param adData  鏇濆厜鐨勫箍鍛婂璞�
       */
      @Override
      public void onADExposure(NativeMediaADData adData) {
        Log.i(TAG, adData.getTitle() + " onADExposure");
      }

      /**
       * 骞垮憡琚偣鍑绘椂鐨勫洖璋�
       *
       * @param adData  琚偣鍑荤殑骞垮憡瀵硅薄
       */
      @Override
      public void onADClicked(NativeMediaADData adData) {
        Log.i(TAG, adData.getTitle() + " onADClicked");
      }
    };

    mADManager = new NativeMediaAD(getApplicationContext(), Constants.APPID, Constants.NativeVideoPosID, listener);
  }

  /**
   * 灏嗗箍鍛婂疄渚嬪拰MediaView缁戝畾锛屾挱鏀捐棰戙��
   *
   * 娉ㄦ剰锛氭挱鏀捐棰戝墠闇�瑕佸皢骞垮憡鐨勫ぇ鍥鹃殣钘忥紝灏哅ediaView鏄剧ず鍑烘潵锛屽惁鍒欒棰戝皢鏃犳硶鎾斁锛屼篃鏃犳硶涓婃姤瑙嗛鏇濆厜锛屾棤娉曚骇鐢熻璐广��
   */
  private void bindMediaView() {
    if (mAD.isVideoAD()) {
      mImagePoster.setVisibility(View.GONE);
      mMediaView.setVisibility(View.VISIBLE);

      /**
       * bindView(MediaView view, boolean useDefaultController):
       *    - useDefaultController: false锛屼笉浼氳皟鐢ㄥ箍鐐归�氱殑榛樿瑙嗛鎺у埗鏉�
       *    - useDefaultController: true锛屼娇鐢⊿DK鍐呯疆鐨勬挱鏀惧櫒鎺у埗鏉★紝姝ゆ椂寮�鍙戣�呴渶瑕佹妸demo涓嬬殑res鏂囦欢澶归噷闈㈢殑鍥剧墖鎷疯礉鍒拌嚜宸遍」鐩殑res鏂囦欢澶瑰幓
       *
       * 鍦ㄨ繖閲岀粦瀹歁ediaView鍚庯紝SDK浼氭牴鎹棰戠礌鏉愮殑瀹介珮姣斾緥锛岄噸鏂扮粰MediaView璁剧疆鏂扮殑瀹介珮
       */
      mAD.bindView(mMediaView, true);
      mAD.play();

      /** 璁剧疆瑙嗛鎾斁杩囩▼涓殑鐩戝惉鍣� */
      mAD.setMediaListener(new MediaListener() {

        /**
         * 瑙嗛鎾斁鍣ㄥ垵濮嬪寲瀹屾垚锛屽噯澶囧ソ鍙互鎾斁浜�
         *
         * @param videoDuration 瑙嗛绱犳潗鐨勬�绘椂闀�
         */
        @Override
        public void onVideoReady(long videoDuration) {
          Log.i(TAG, "onVideoReady, videoDuration = " + videoDuration);
          duration = videoDuration;
        }

        /** 瑙嗛寮�濮嬫挱鏀� */
        @Override
        public void onVideoStart() {
          Log.i(TAG, "onVideoStart");
          tikTokHandler.post(countDown);
          mTextCountDown.setVisibility(View.VISIBLE);
        }

        /** 瑙嗛鏆傚仠 */
        @Override
        public void onVideoPause() {
          Log.i(TAG, "onVideoPause");
          mTextCountDown.setVisibility(View.GONE);
        }

        /** 瑙嗛鑷姩鎾斁缁撴潫锛屽埌杈炬渶鍚庝竴甯� */
        @Override
        public void onVideoComplete() {
          Log.i(TAG, "onVideoComplete");
          releaseCountDown();
          mTextCountDown.setVisibility(View.GONE);
        }

        /** 瑙嗛鎾斁鏃跺嚭鐜伴敊璇� */
        @Override
        public void onVideoError(int errorCode) {
          Log.i(TAG, "onVideoError, errorCode: " + errorCode);
        }

        /** SDK鍐呯疆鐨勬挱鏀惧櫒鎺у埗鏉′腑鐨勯噸鎾寜閽鐐瑰嚮 */
        @Override
        public void onReplayButtonClicked() {
          Log.i(TAG, "onReplayButtonClicked");
        }

        /**
         * SDK鍐呯疆鐨勬挱鏀惧櫒鎺у埗鏉′腑鐨勪笅杞�/鏌ョ湅璇︽儏鎸夐挳琚偣鍑�
         * 娉ㄦ剰: 杩欓噷鏄寚UI涓殑鎸夐挳琚偣鍑讳簡锛岃�屽箍鍛婄殑鐐瑰嚮浜嬩欢鍥炶皟鏄湪onADClicked涓紝寮�鍙戣�呭闇�缁熻鐐瑰嚮鍙渶瑕佸湪onADClicked鍥炶皟涓繘琛屼竴娆＄粺璁″嵆鍙��
         */
        @Override
        public void onADButtonClicked() {
          Log.i(TAG, "onADButtonClicked");
        }

        /** SDK鍐呯疆鐨勫叏灞忓拰闈炲叏灞忓垏鎹㈠洖璋冿紝杩涘叆鍏ㄥ睆鏃秈nFullScreen涓簍rue锛岄��鍑哄叏灞忔椂inFullScreen涓篺alse */
        @Override
        public void onFullScreenChanged(boolean inFullScreen) {
          Log.i(TAG, "onFullScreenChanged, inFullScreen = " + inFullScreen);

          // 鍘熺敓瑙嗛骞垮憡榛樿闈欓煶鎾斁锛岃繘鍏ュ埌鍏ㄥ睆鍚庡缓璁紑鍙戣�呭彲浠ヨ缃负鏈夊０鎾斁
          if (inFullScreen) {
            mAD.setVolumeOn(true);
          } else {
            mAD.setVolumeOn(false);
          }
        }
      });
    }
  }

  private void initADUI() {
    mAQuery.id(R.id.img_logo).image(mAD.getIconUrl(), false, true);
    mAQuery.id(R.id.img_poster).image(mAD.getImgUrl(), false, true, 0, 0, new BitmapAjaxCallback() {
      @Override
      protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
        // AQuery妗嗘灦鏈変竴涓棶棰橈紝灏辨槸鍗充娇鍦ㄥ浘鐗囧姞杞藉畬鎴愪箣鍓嶅皢ImageView璁剧疆涓轰簡View.GONE锛屽湪鍥剧墖鍔犺浇瀹屾垚鍚庯紝杩欎釜ImageView浼氳閲嶆柊璁剧疆涓篤IEW.VISIBLE銆�
        // 鎵�浠ュ湪杩欓噷闇�瑕佸垽鏂竴涓嬶紝濡傛灉宸茬粡鎶奍mageView璁剧疆涓洪殣钘忥紝寮�濮嬫挱鏀捐棰戜簡锛屽氨涓嶈鍐嶆樉绀哄箍鍛婄殑澶у浘銆傚惁鍒欏皢褰卞搷鍒皊dk鐨勬洕鍏変笂鎶ワ紝鏃犳硶浜х敓鏀剁泭銆�
        // 寮�鍙戣�呭湪鐢ㄥ叾浠栫殑鍥剧墖鍔犺浇妗嗘灦鏃讹紝涔熷簲璇ユ敞鎰忔鏌ヤ笅鏄惁鏈夎繖涓棶棰樸��
        if (iv.getVisibility() == View.VISIBLE) {
          iv.setImageBitmap(bm);
        }
      }
    });
    mAQuery.id(R.id.text_title).text(mAD.getTitle());
    mAQuery.id(R.id.text_desc).text(mAD.getDesc());
    updateADUI();
    /**
     * 娉ㄦ剰锛氬湪娓叉煋鏃讹紝蹇呴』鍏堣皟鐢╫nExposured鎺ュ彛鏇濆厜骞垮憡锛屽惁鍒欑偣鍑绘帴鍙nClicked灏嗘棤鏁�
     */
    mAD.onExposured(mADInfoContainer);
    mDownloadButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        mAD.onClicked(view);
      }
    });
  }

  private void updateADUI() {
    if (!mAD.isAPP()) {
      mDownloadButton.setText("娴忚");
      return;
    }
    switch (mAD.getAPPStatus()) {
      case 0:
        mDownloadButton.setText("涓嬭浇");
        break;
      case 1:
        mDownloadButton.setText("鍚姩");
        break;
      case 2:
        mDownloadButton.setText("鏇存柊");
        break;
      case 4:
        mDownloadButton.setText(mAD.getProgress() + "%");
        break;
      case 8:
        mDownloadButton.setText("瀹夎");
        break;
      case 16:
        mDownloadButton.setText("涓嬭浇澶辫触锛岄噸鏂颁笅杞�");
        break;
      default:
        mDownloadButton.setText("娴忚");
        break;
    }
  }

  private void loadAD() {
    if (mADManager != null) {
      try {
        mADManager.loadAD(AD_COUNT);
      } catch (Exception e) {
        Toast.makeText(getApplicationContext(), "鍔犺浇澶辫触", Toast.LENGTH_SHORT).show();
      }
    }
  }

  /**
   * 鍦ˋctivity鐢熷懡鍛ㄦ湡涓竴瀹氳璋冪敤瑙嗛骞垮憡鐨勬殏鍋滃拰鎾斁鎺ュ彛锛屼繚璇佺敤鎴峰湪Activity涔嬮棿鍒囨崲鏃讹紝瑙嗛鍙互鏆傚仠鍜岀画鎾紝鍚屾椂涔熶繚璇佹暟鎹笂鎶ユ甯革紝鍚﹀垯灏嗗奖鍝嶅埌寮�鍙戣�呯殑鏀剁泭锛�
   *    - Activity.onResume() 锛�  璋冪敤NativeMediaADData.resume()
   *    - Activity.onPause()  锛�  璋冪敤NativeMediaADData.stop()锛屽箍鐐归�氱洰鍓嶅湪浜у搧姒傚康涓婅姹傛瘡娆¤鐪嬭棰戝箍鍛婇兘瑕侀噸澶存挱鏀撅紝鐩墠涓嶆彁渚涙殏鍋滄帴鍙ｃ�傝皟鐢╯top鎺ュ彛锛屼竴涓綔鐢ㄦ槸淇濊瘉鐢ㄦ埛浣撻獙锛屽綋鐢ㄦ埛鐪嬩笉鍒板綋鍓嶇晫闈㈡椂涓嶈鍐嶆挱鏀捐棰戙�傚彟涓�涓綔鐢ㄦ槸瑙﹀彂SDK鐨勬挱鏀句俊鎭笂鎶ヤ簨浠讹紝淇濊瘉姣忔瑙傜湅閮芥湁鏁堟灉涓婃姤銆�
   *    - Activity.onDestroy()锛�  璋冪敤NativeMediaADData.destroy()閲婃斁璧勬簮锛屼笉鍐嶆挱鏀捐棰戝箍鍛婃椂锛屼竴瀹氳璁板緱璋冪敤銆�
   */
  @Override
  protected void onResume() {
    Log.i(TAG, "onResume");
    if (mAD != null) {
      mAD.resume();
    }
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    if (mAD != null) {
      mAD.stop();
    }
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    if (mAD != null) {
      mAD.destroy();
    }
    releaseCountDown();
    super.onDestroy();
  }

  /**
   * 鍒锋柊骞垮憡鍊掕鏃讹紝鏈ず渚嬫彁渚涚殑鎬濊矾浠呭紑鍙戣�呬緵鍙傝�冿紝寮�鍙戣�呭畬鍏ㄥ彲浠ユ牴鎹嚜宸辩殑闇�姹傝璁′笉鍚岀殑鏍峰紡銆�
   */
  private static final String TEXT_COUNTDOWN = "骞垮憡鍊掕鏃讹細%s ";
  private long currentPosition, oldPosition, duration;
  private Runnable countDown = new Runnable() {
    public void run() {
      if (mAD != null) {
        currentPosition = mAD.getCurrentPosition();
        long position = currentPosition;
        if (oldPosition == position && mAD.isPlaying()) {
          Log.d(TAG, "鐜╁懡鍔犺浇涓�...");
          mTextCountDown.setTextColor(Color.WHITE);
          mTextCountDown.setText("鐜╁懡鍔犺浇涓�...");
        } else {
          Log.d(TAG, String.format(TEXT_COUNTDOWN, Math.round((duration - position) / 1000.0) + ""));
          mTextCountDown.setText(String.format(TEXT_COUNTDOWN, Math.round((duration - position) / 1000.0) + ""));
        }
        oldPosition = position;
        if (mAD.isPlaying()) {
          tikTokHandler.postDelayed(countDown, 500); // 500ms鍒锋柊涓�娆¤繘搴﹀嵆鍙�
        }
      }
    }

  };

  private void releaseCountDown() {
    if (countDown != null) {
      tikTokHandler.removeCallbacks(countDown);
    }
  }

}
