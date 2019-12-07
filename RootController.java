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
      // ��� �ҽ�(Media)�� ������
       Media media = new Media(getClass().getResource("images/�ʹ��ʹ��ʹ�.mp3").toString());
      //Media media = new Media(getClass().getResource("media/�ӿ�ȯ����.mp4").toString());
      // MediaPlayer�� ����� �ҽ��� �Ű����� ������ MediaPlayer�� ������.
       mediaPlayer = new MediaPlayer(media);
      // MediaPlayer�� ����ϴ� ������ mediaView�� �����ְ��� ������.
      mediaView.setMediaPlayer(mediaPlayer);

      // �ҽ��м��� ������ Ready()���°� �Ǹ� �Ʒ��� ���� �ڵ�����
      mediaPlayer.setOnReady(new Runnable() {
         @Override
         public void run() {
            // setDisable�޼��� �Ű����� true�̸� ��Ȱ��ȭ, false�̸� Ȱ��ȭ�� ���ش�.
            btnPlay.setDisable(false);
            btnPause.setDisable(true);
            btnStop.setDisable(true);

            // ��������� �̵����̴��� �ְ��� �ּҰ��� �ʴ����� ������.
            sliderMedia.setMin(0.0);
            sliderMedia.setValue(0.0);
            sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());
            /*
             * MedaiPlayer�� ����ð��� �Ӽ����ø� �Ѵ�. �ð��̱⿡ ���׸�Ÿ����Duration �̴�. ����� �Ǹ鼭 currentTime��
             * ����Ǹ�, �� ����newValue�� ��� ���� �ȴ�. �� currentTime�� ProgressBar��
             * ProgressIndicator�� ��Ÿ���� ���ؼ��� 0.0~1.0������ ������ ȯ���Ͽ� ��Ÿ���� ��� �Ѵ�.
             */

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
               @Override
               public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
                     Duration newValue) {
                  // ���� ����ð� /�� ��� �ð��� �ϸ� 0.0~1.0�� ���� ���̴�.
                  double progress = mediaPlayer.getCurrentTime().toSeconds()
                        / mediaPlayer.getTotalDuration().toSeconds();

                  // 0.0~1.0������ ���� ���� ������ �ش�.
                  progressBar.setProgress(progress);
                  // �������� ��������� ��Ÿ����.
                  sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

                  // ����ð��� Label�� ǥ���ϱ� ���� ����(doubleŸ���� �������� intŸ������
                  // ������ȯ)
                  labelTime.setText((int) mediaPlayer.getCurrentTime().toSeconds() + "/"
                        + (int) mediaPlayer.getTotalDuration().toSeconds() + "��");

                  sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

               }
            });

         }
      });

      // �̵�� �ҽ��� ����ǰ� �������� �����ؼ� ������ ������ ���ٽ����� ������
      mediaPlayer.setOnPlaying(() -> {
    	  btnPlay.setDisable(true); // Play��ư ��Ȱ��ȭ
			btnPause.setDisable(false); // Pause��ư Ȱ��ȭ
			btnStop.setDisable(false);
      });

      mediaPlayer.setOnPaused(() -> {
    	  btnPlay.setDisable(false); // Play��ư Ȱ��ȭ
			btnPause.setDisable(true); // Pause��ư ��Ȱ��ȭ
			btnStop.setDisable(false); 
      });

      mediaPlayer.setOnStopped(() -> {
    	  btnPlay.setDisable(false); // Play��ư Ȱ��ȭ
			btnPause.setDisable(true); // Pause��ư ��Ȱ��ȭ
			btnStop.setDisable(true);
      });

      mediaPlayer.setOnEndOfMedia(() -> {
    	  btnPlay.setDisable(false); // Play��ư Ȱ��ȭ
			btnPause.setDisable(true); // Pause��ư ��Ȱ��ȭ
			btnStop.setDisable(true);

         // ������ ����� �Ϸᰡ �Ǿ �����ư�� Ȱ��ȭ�� �Ǿ� Ŭ���� ������,.
         // ����� ���� �ʴ´� ������ ����Ϸ� ������ �� �ҽ��� ���°� �ӹ���
         // �ֱ� �����̴�. �Ͽ�, ��������� stop()�� ȣ���ϰ� �� �ҽ��� �ð���
         // �� ÷���� �����ִ� �ڵ带 �ڵ��ؾ��Ѵ�. �Ͽ�, ���� ������ �÷���
         // ������ true�� �����Ѵ�.
         endofMedia = true;

         // ���������� 1.0�� �����Ͽ� �Ϸ�(Done)���°� �������� ����.
         progressBar.setProgress(1.0);

      });
//�������� �� �÷��̵Ǿ����� �̺�Ʈ ó��
      btnPlay.setOnAction(event -> {
         if (endofMedia) {
            mediaPlayer.stop();
            mediaPlayer.seek(mediaPlayer.getStartTime());
            endofMedia = false; // �÷��׺����� �ǵ�����.
         }
         mediaPlayer.play();
      });
      /*
       * SliderVolume�� ����Ǿ����� ������ ũ�⸦ �����ϴ� �Ӽ����� Slider�� value�� ������ 0.0~100.0�̴� ������,
       * mediaPlayer�� value�� ������ 0.0~1.0�̱� ������ 100.0���� ������.
       * 
       */
      // ����������Ʈ
      sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            // mediaPlaye.setVolume(newValue.doubleValue()/100.0);
            // ����������
            double volume = sliderVolume.getValue() / 100.0;
            mediaPlayer.setVolume(volume);
         }
      });

      // ����Ŭ����
//      sliderMedia.setOnMouseClicked(new EventHandler<MouseEvent>() {
//         @Override
//         public void handle(MouseEvent event) {
//            mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
//
//         }
//      });

//      //�̺κ��� ����� �����ϱ�.
         sliderMedia.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, 
                           Number oldValue, Number newValue) {
               //���콺�� �巡���� ��
               if(sliderMedia.isValueChanging()) {
                  mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
               }
               //���� ��� �Ǵ� ���콺�� Ŭ���� ��
               else {   
                    //���� ����� ���(��ȭ���� 0.5 ����) seek ���� ����
                  if (Math.abs(oldValue.doubleValue() - newValue.doubleValue()) > 0.5) {
                      mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));            
                  }
               }            
            }
         });

      // ���2
//      sliderMedia.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
//                  Number newValue) {
//               if(sliderMedia.isValueChanging()|| sliderMedia.isPressed()) { //isValueChanging : ���콺 �巡������ ��� || isPressed : ���콺�� Ŭ���ߴ°�?
//               mediaPlayer.seek(Duration.seconds((double)newValue));  // newValue : Ŭ�� || �巡�� ������� ��������ġ���ִ� ��   seek : �� �ð���ġ���� ã�Ƽ� ����
//               }
//            }
//         });

      // �������� 50���� �⺻����
      sliderVolume.setValue(50.0);
      // Pause��ư�� Ŭ�������� �������� �Ͻ������ϴ� �̺�Ʈó�� �ڵ�
      btnPause.setOnAction(event -> mediaPlayer.stop());
      // Stop��ư�� Ŭ�������� �������� �Ͻ������ϴ� �̺�Ʈó�� �ڵ�
      btnStop.setOnAction(event -> mediaPlayer.pause());

   }
   // �޴��� �̺�Ʈ ó��

   public void handleOpenFileChooser(ActionEvent event) {
	   FileChooser fileChooser = new FileChooser();
		// getExtensionFilters()�� ����Ȯ����� �������� ���͸��� ���ִ� �޼����̸�,
		// �ƿ﷯ �Ű����� ExtensionFilterŬ������ ������.
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Media Files(*.mp4,*.avi,*.mkv)", "*.mp4", "*.avi", "*.mkv"),
				new ExtensionFilter("Audio Files(*.mp3,*.wav,*.aac)", "*.mp3", "*.wav", "*.aac"),
				new ExtensionFilter("All Files(*.*)", "*.*"));

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {

			mediaPlayer.stop();
			mediaPlayer.seek(mediaPlayer.getStartTime());
			endofMedia = false; // �÷��׺����� �ǵ�����.

			// mediaView.
			media = new Media(selectedFile.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			initButton();
		}
	}
   public void initButton() {
		// �ҽ��м��� ������ Ready()���°� �Ǹ� �Ʒ��� ���� �ڵ�����
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				// setDisable�޼��� �Ű����� true�̸� ��Ȱ��ȭ, false�̸� Ȱ��ȭ�� ���ش�.
				btnPlay.setDisable(false);
				btnPause.setDisable(true);
				btnStop.setDisable(true);

				// ��������� �̵����̴��� �ְ��� �ּҰ��� �ʴ����� ������.
				sliderMedia.setMin(0.0);
				sliderMedia.setValue(0.0);
				sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());
				/*
				 * MedaiPlayer�� ����ð��� �Ӽ����ø� �Ѵ�. �ð��̱⿡ ���׸�Ÿ����Duration �̴�. ����� �Ǹ鼭 currentTime��
				 * ����Ǹ�, �� ����newValue�� ��� ���� �ȴ�. �� currentTime�� ProgressBar��
				 * ProgressIndicator�� ��Ÿ���� ���ؼ��� 0.0~1.0������ ������ ȯ���Ͽ� ��Ÿ���� ��� �Ѵ�.
				 */

				mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
					@Override
					public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
							Duration newValue) {
						// ���� ����ð� /�� ��� �ð��� �ϸ� 0.0~1.0�� ���� ���̴�.
						double progress = mediaPlayer.getCurrentTime().toSeconds()
								/ mediaPlayer.getTotalDuration().toSeconds();

						// 0.0~1.0������ ���� ���� ������ �ش�.
						progressBar.setProgress(progress);
						
						// �������� ��������� ��Ÿ����.
						sliderMedia.setValue(mediaPlayer.getCurrentTime().toSeconds());

						// ����ð��� Label�� ǥ���ϱ� ���� ����(doubleŸ���� �������� intŸ������
						// (������ȯ)
						labelTime.setText((int) mediaPlayer.getCurrentTime().toSeconds() + "/"
								+ (int) mediaPlayer.getTotalDuration().toSeconds() + "��");

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

			// ������ ����� �Ϸᰡ �Ǿ �����ư�� Ȱ��ȭ�� �Ǿ� Ŭ���� ������,.
			// ����� ���� �ʴ´� ������ ����Ϸ� ������ �� �ҽ��� ���°� �ӹ���
			// �ֱ� �����̴�. �Ͽ�, ��������� stop()�� ȣ���ϰ� �� �ҽ��� �ð���
			// �� ÷���� �����ִ� �ڵ带 �ڵ��ؾ��Ѵ�. �Ͽ�, ���� ������ �÷���
			// ������ true�� �����Ѵ�.
			endofMedia = true;

			// ���������� 1.0�� �����Ͽ� �Ϸ�(Done)���°� �������� ����.
			progressBar.setProgress(1.0);

		});
		sliderMedia.setMin(0.0);
		sliderMedia.setValue(0.0);
		sliderMedia.setMax(mediaPlayer.getTotalDuration().toSeconds());

	}

}