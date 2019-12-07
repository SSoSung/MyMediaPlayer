package My_MeidaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RootController implements Initializable {

   @FXML private Button btnPlay;
   @FXML private Button btnStop;
   @FXML private Button btnPause;
   
   @FXML private Label labelTime;
   
   @FXML private ProgressBar progressBar;
   @FXML private ImageView imageView;
   @FXML private MediaView mediaView;
   @FXML private Slider sliderMedia;
   @FXML private Slider sliderVolume;
   
   boolean endofMedia;

   private Stage primaryStage;
   static MediaPlayer mediaPlayer;
   static Media media;
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      // 재생 소스(Media)을 생성함
       Media media = new Media(getClass().getResource("images/너무너무너무.mp3").toString());
      //Media media = new Media(getClass().getResource("media/임요환전략.mp4").toString());
      // MediaPlayer가 재새할 소스를 매개값을 가지고 MediaPlayer를 생성함.
       mediaPlayer = new MediaPlayer(media);
      // MediaPlayer가 재생하는 내용을 mediaView에 보여주고자 설정함.
      mediaView.setMediaPlayer(mediaPlayer);

      // 소스분석이 끝나고 Ready()상태가 되면 아래와 같이 자동실행
      mediaPlayer.setOnReady(new Runnable() {
         @Override
         public void run() {
            // setDisable메서드 매개값이 true이면 비활성화, false이면 활성화를 해준다.
            btnPlay.setDisable(false);
            btnPause.setDisable(true);
            btnStop.setDisable(true);

            // 재생정도를 미디어슬라이더에 최고값과 최소값을 초단위로 매핑함.
            sliderMedia.setMin(0.0);
            sliderMedia.setValue(0.0);
            sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());
            /*
             * MedaiPlayer의 현재시간을 속성감시를 한다. 시간이기에 제네릭타입은Duration 이다. 재생이 되면서 currentTime이
             * 변경되면, 그 값은newValue에 계속 들어가게 된다. 이 currentTime을 ProgressBar나
             * ProgressIndicator에 나타내기 위해서는 0.0~1.0사이의 값으로 환산하여 나타내어 줘야 한다.
             */

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
               @Override
               public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
                     Duration newValue) {
                  // 현재 재생시간 /총 재생 시간을 하면 0.0~1.0이 나올 것이다.
                  double progress = mediaPlayer.getCurrentTime().toSeconds()
                        / mediaPlayer.getTotalDuration().toSeconds();

                  // 0.0~1.0사이의 값을 각각 설정해 준다.
                  progressBar.setProgress(progress);
                  // 동영상의 재생정도를 나타낸다.
                  sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

                  // 재생시간을 Label에 표시하기 위한 설정(double타입을 보기좋게 int타입으로
                  // 강제변환)
                  labelTime.setText((int) mediaPlayer.getCurrentTime().toSeconds() + "/"
                        + (int) mediaPlayer.getTotalDuration().toSeconds() + "초");

                  sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

               }
            });

         }
      });

      // 미디어 소스가 실행되고 있을때를 감지해서 실행할 내용을 람다식으로 제공함
      mediaPlayer.setOnPlaying(() -> {
    	  btnPlay.setDisable(true); // Play버튼 비활성화
			btnPause.setDisable(false); // Pause버튼 활성화
			btnStop.setDisable(false);
      });

      mediaPlayer.setOnPaused(() -> {
    	  btnPlay.setDisable(false); // Play버튼 활성화
			btnPause.setDisable(true); // Pause버튼 비활성화
			btnStop.setDisable(false); 
      });

      mediaPlayer.setOnStopped(() -> {
    	  btnPlay.setDisable(false); // Play버튼 활성화
			btnPause.setDisable(true); // Pause버튼 비활성화
			btnStop.setDisable(true);
      });

      mediaPlayer.setOnEndOfMedia(() -> {
    	  btnPlay.setDisable(false); // Play버튼 활성화
			btnPause.setDisable(true); // Pause버튼 비활성화
			btnStop.setDisable(true);

         // 문제는 재생이 완료가 되어도 재생버튼이 활성화가 되어 클릭이 되지만,.
         // 재생이 되질 않는다 이유는 재생완료 시점에 그 소스의 상태가 머물러
         // 있기 때문이다. 하여, 명시적으로 stop()을 호출하고 그 소스의 시간을
         // 맨 첨으로 돌려주는 코드를 코딩해야한다. 하여, 위에 선언한 플래그
         // 변수를 true로 설정한다.
         endofMedia = true;

         // 강제적으로 1.0을 설정하여 완료(Done)상태가 나오도록 하자.
         progressBar.setProgress(1.0);

      });
//동영상이 다 플레이되었을때 이벤트 처리
      btnPlay.setOnAction(event -> {
         if (endofMedia) {
            mediaPlayer.stop();
            mediaPlayer.seek(mediaPlayer.getStartTime());
            endofMedia = false; // 플래그변수로 되돌린다.
         }
         mediaPlayer.play();
      });
      /*
       * SliderVolume이 변경되었을때 볼륨의 크기를 조절하는 속성감시 Slider의 value의 범위는 0.0~100.0이다 하지만,
       * mediaPlayer의 value의 범위는 0.0~1.0이기 때문에 100.0으로 나눈다.
       * 
       */
      // 볼륨조정파트
      sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            // mediaPlaye.setVolume(newValue.doubleValue()/100.0);
            // 볼륨조정값
            double volume = sliderVolume.getValue() / 100.0;
            mediaPlayer.setVolume(volume);
         }
      });

      // 더블클릭시
//      sliderMedia.setOnMouseClicked(new EventHandler<MouseEvent>() {
//         @Override
//         public void handle(MouseEvent event) {
//            mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
//
//         }
//      });

//      //이부분은 퀴즈로 출제하기.
         sliderMedia.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, 
                           Number oldValue, Number newValue) {
               //마우스로 드래그할 때
               if(sliderMedia.isValueChanging()) {
                  mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
               }
               //정상 재생 또는 마우스로 클릭할 때
               else {   
                    //정상 재생일 경우(변화값이 0.5 이하) seek 하지 않음
                  if (Math.abs(oldValue.doubleValue() - newValue.doubleValue()) > 0.5) {
                      mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));            
                  }
               }            
            }
         });

      // 방법2
//      sliderMedia.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
//                  Number newValue) {
//               if(sliderMedia.isValueChanging()|| sliderMedia.isPressed()) { //isValueChanging : 마우스 드래그했을 경우 || isPressed : 마우스를 클릭했는가?
//               mediaPlayer.seek(Duration.seconds((double)newValue));  // newValue : 클릭 || 드래그 했을경우 마지막위치에있는 값   seek : 그 시간위치에를 찾아서 실행
//               }
//            }
//         });

      // 볼륨값을 50으로 기본지정
      sliderVolume.setValue(50.0);
      // Pause버튼을 클릭했을때 동영상이 일시정지하는 이벤트처리 코드
      btnPause.setOnAction(event -> mediaPlayer.stop());
      // Stop버튼을 클릭했을때 동영상이 일시정지하는 이벤트처리 코드
      btnStop.setOnAction(event -> mediaPlayer.pause());

   }
   // 메뉴바 이벤트 처리

   public void handleOpenFileChooser(ActionEvent event) {
	   FileChooser fileChooser = new FileChooser();
		// getExtensionFilters()는 파일확장명을 기준으로 필터링을 해주는 메서드이며,
		// 아울러 매개값을 ExtensionFilter클래스를 가진다.
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Media Files(*.mp4,*.avi,*.mkv)", "*.mp4", "*.avi", "*.mkv"),
				new ExtensionFilter("Audio Files(*.mp3,*.wav,*.aac)", "*.mp3", "*.wav", "*.aac"),
				new ExtensionFilter("All Files(*.*)", "*.*"));

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {

			mediaPlayer.stop();
			mediaPlayer.seek(mediaPlayer.getStartTime());
			endofMedia = false; // 플래그변수로 되돌린다.

			// mediaView.
			media = new Media(selectedFile.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			initButton();
		}
	}
   public void initButton() {
		// 소스분석이 끝나고 Ready()상태가 되면 아래와 같이 자동실행
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				// setDisable메서드 매개값이 true이면 비활성화, false이면 활성화를 해준다.
				btnPlay.setDisable(false);
				btnPause.setDisable(true);
				btnStop.setDisable(true);

				// 재생정도를 미디어슬라이더에 최고값과 최소값을 초단위로 매핑함.
				sliderMedia.setMin(0.0);
				sliderMedia.setValue(0.0);
				sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());
				/*
				 * MedaiPlayer의 현재시간을 속성감시를 한다. 시간이기에 제네릭타입은Duration 이다. 재생이 되면서 currentTime이
				 * 변경되면, 그 값은newValue에 계속 들어가게 된다. 이 currentTime을 ProgressBar나
				 * ProgressIndicator에 나타내기 위해서는 0.0~1.0사이의 값으로 환산하여 나타내어 줘야 한다.
				 */

				mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
					@Override
					public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
							Duration newValue) {
						// 현재 재생시간 /총 재생 시간을 하면 0.0~1.0이 나올 것이다.
						double progress = mediaPlayer.getCurrentTime().toSeconds()
								/ mediaPlayer.getTotalDuration().toSeconds();

						// 0.0~1.0사이의 값을 각각 설정해 준다.
						progressBar.setProgress(progress);
						
						// 동영상의 재생정도를 나타낸다.
						sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

						// 재생시간을 Label에 표시하기 위한 설정(double타입을 보기좋게 int타입으로
						// (강제변환)
						labelTime.setText((int) mediaPlayer.getCurrentTime().toSeconds() + "/"
								+ (int) mediaPlayer.getTotalDuration().toSeconds() + "초");

						sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

					}
				});

			}
		});

		mediaPlayer.setOnPlaying(() -> {
			btnPlay.setDisable(true);
			btnPause.setDisable(false);
			btnStop.setDisable(false);
		});

		mediaPlayer.setOnPaused(() -> {
			btnPlay.setDisable(false);
			btnPause.setDisable(true);
			btnStop.setDisable(false);
		});

		mediaPlayer.setOnStopped(() -> {
			btnPlay.setDisable(false);
			btnPause.setDisable(false);
			btnStop.setDisable(true);
		});

		mediaPlayer.setOnEndOfMedia(() -> {
			btnPlay.setDisable(true);
			btnPause.setDisable(false);
			btnStop.setDisable(false);

			// 문제는 재생이 완료가 되어도 재생버튼이 활성화가 되어 클릭이 되지만,.
			// 재생이 되질 않는다 이유는 재생완료 시점에 그 소스의 상태가 머물러
			// 있기 때문이다. 하여, 명시적으로 stop()을 호출하고 그 소스의 시간을
			// 맨 첨으로 돌려주는 코드를 코딩해야한다. 하여, 위에 선언한 플래그
			// 변수를 true로 설정한다.
			endofMedia = true;

			// 강제적으로 1.0을 설정하여 완료(Done)상태가 나오도록 하자.
			progressBar.setProgress(1.0);

		});
		sliderMedia.setMin(0.0);
		sliderMedia.setValue(0.0);
		sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());

	}

}