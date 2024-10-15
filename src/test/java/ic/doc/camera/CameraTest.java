package ic.doc.camera;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
  public void cameraIsPoweredOffByDefault() {
    assertThat(camera.isPoweredOn(), is(false));
  }

  @Test
  public void cameraCanBePoweredOnAndOff() {
    context.checking(new Expectations() {{
      oneOf(sensor).powerUp();
      oneOf(sensor).powerDown();
    }});

    // check the state of the camera after powering on
    camera.powerOn();
    assertThat(camera.isPoweredOn(), is(true));

    // check the state of the camera after powering off
    camera.powerOff();
    assertThat(camera.isPoweredOn(), is(false));
  }

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {
    context.checking(new Expectations() {{
      oneOf(sensor).powerUp();
    }});

    camera.powerOn();
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {
    context.checking(new Expectations() {{
      oneOf(sensor).powerDown();
    }});

    camera.powerOff();
  }

  @Test
  public void pressingTheShutterWhenThePowerIsOffDoesNothing() {
    context.checking(new Expectations() {{
      never(memoryCard);

      oneOf(sensor).powerDown();
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

  @Test
  public void pressingTheShutterWithThePowerOn_copiesTheDataFromTheSensorToTheMemoryCard() {
    // Example data that readData() could return
    final byte[] mockData = new byte[]{1, 2, 3, 4};

    context.checking(new Expectations() {{
      oneOf(sensor).powerUp();

      oneOf(sensor).readData(); will(returnValue(mockData));
      oneOf(memoryCard).write(with(equal(mockData)));
    }});

    // power on the camera to ensure the test performs
    // as intended without side effects
    // expect 1x `sensor.powerUp()` for this
    camera.powerOn();

    // now, shutter should copy data from the sensor to the memory card
    camera.pressShutter();
  }
}
