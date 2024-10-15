package ic.doc.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  Sensor sensor = context.mock(Sensor.class);
  Camera camera = new Camera(sensor);

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {
    context.checking(new Expectations() {{
      exactly(1).of(sensor).powerUp();
    }});

    camera.powerOn();
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {
    context.checking(new Expectations() {{
      exactly(1).of(sensor).powerDown();
    }});

    camera.powerOff();
  }

}
