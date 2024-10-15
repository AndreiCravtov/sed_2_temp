package ic.doc.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  MemoryCard memoryCard = context.mock(MemoryCard.class);
  Sensor sensor = context.mock(Sensor.class);
  Camera camera = new Camera(memoryCard, sensor);

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

  @Test
  public void pressingTheShutterWhenThePowerIsOffDoesNothing() {
    context.checking(new Expectations() {{
      never(memoryCard);

      exactly(1).of(sensor).powerDown();
      never(sensor).powerUp();
      never(sensor).readData();
    }});

    // power off the camera to ensure the test performs
    // as intended without side effects
    // expect 1x `sensor.powerDown()` for this
    camera.powerOff();

    // now, shutter should do nothing
    camera.pressShutter();
  }
}
