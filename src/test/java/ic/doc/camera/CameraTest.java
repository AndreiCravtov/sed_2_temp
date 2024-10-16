package ic.doc.camera;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.function.BiConsumer;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;


public class CameraTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  // Set up memory card mocks and proxies
  MemoryCard setupMemoryCard = data -> {
  };
  MemoryCard testingMemoryCard = context.mock(MemoryCard.class);
  MemoryCardBinarySwitchProxy memoryCardBinarySwitch = new MemoryCardBinarySwitchProxy(
      setupMemoryCard, testingMemoryCard, true);

  // Set up sensor mocks and proxies
  Sensor setupSensor = new Sensor() {
    @Override
    public byte[] readData() {
      return new byte[0];
    }

    @Override
    public void powerUp() {
    }

    @Override
    public void powerDown() {
    }
  };
  Sensor testingSensor = context.mock(Sensor.class);
  SensorBinarySwitchProxy sensorBinarySwitch = new SensorBinarySwitchProxy(
      setupSensor, testingSensor, true);

  // Use binary switch proxies to set up camera
  Camera camera = new Camera(memoryCardBinarySwitch, sensorBinarySwitch);

  // Convenience methods for synchronising the binary switches
  void useSetupMockObjects() {
    memoryCardBinarySwitch.useFirstMemoryCard();
    sensorBinarySwitch.useFirstSensor();
  }

  void useTestingMockObjects() {
    memoryCardBinarySwitch.useSecondMemoryCard();
    sensorBinarySwitch.useSecondSensor();
  }

  void defineTest(Runnable setup, BiConsumer<MemoryCard, Sensor> testing) {
    // setup phase
    setup.run();

    // switch to testing mock objects and run testing
    useTestingMockObjects();
    testing.accept(testingMemoryCard, testingSensor);
  }

  @Test
  public void cameraIsPoweredOffByDefault() {
    defineTest(() -> { // SETUP

    }, (memoryCard, sensor) -> { // TESTING

      // Camera should send signal to sensor to power down,
      // in case it wasn't off already
      context.checking(new Expectations() {{
        oneOf(sensor).powerDown();
      }});

      Camera constructedCamera = new Camera(memoryCard, sensor);
      assertThat(constructedCamera.isPoweredOn(), is(false));

    });
  }

  @Test
  public void cameraCanBePoweredOnAndOff() {
    defineTest(() -> { // SETUP

      // power off the camera to ensure the test performs
      // as intended without side effects
      camera.powerOff();

    }, (memoryCard, sensor) -> { // TESTING

      Sequence powerOnOffSequence = context.sequence("powerOnOffSequence");

      context.checking(new Expectations() {{
        oneOf(sensor).powerUp();
        inSequence(powerOnOffSequence);
        oneOf(sensor).powerDown();
        inSequence(powerOnOffSequence);
      }});

      // check the state of the camera after powering on
      camera.powerOn();
      assertThat(camera.isPoweredOn(), is(true));

      // check the state of the camera after powering off
      camera.powerOff();
      assertThat(camera.isPoweredOn(), is(false));

    });
  }

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {
    defineTest(() -> { // SETUP

      // power off the camera to ensure the test performs
      // as intended without side effects
      camera.powerOff();

    }, (memoryCard, sensor) -> { // TESTING

      context.checking(new Expectations() {{
        oneOf(sensor).powerUp();
      }});

      camera.powerOn();

    });
  }

  @Test
  public void switchingTheCameraOnWhenItsAlreadyOnDoesNothing() {
    defineTest(() -> { // SETUP

      // power on the camera to ensure the test performs
      // as intended without side effects
      camera.powerOn();

    }, (memoryCard, sensor) -> { // TESTING

      context.checking(new Expectations() {{
        never(memoryCard);
        never(sensor);
      }});

      camera.powerOn();

    });
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {
    defineTest(() -> { // SETUP

      // power on the camera to ensure the test performs
      // as intended without side effects
      camera.powerOn();

    }, (memoryCard, sensor) -> { // TESTING

      context.checking(new Expectations() {{
        oneOf(sensor).powerDown();
      }});

      camera.powerOff();

    });
  }

  @Test
  public void switchingTheCameraOffWhenItsAlreadyOffDoesNothing() {
    defineTest(() -> { // SETUP

      // power off the camera to ensure the test performs
      // as intended without side effects
      camera.powerOff();

    }, (memoryCard, sensor) -> { // TESTING

      context.checking(new Expectations() {{
        never(memoryCard);
        never(sensor);
      }});

      camera.powerOff();

    });
  }

  @Test
  public void pressingTheShutterWhenThePowerIsOffDoesNothing() {
    defineTest(() -> { // SETUP

      // power off the camera to ensure the test performs
      // as intended without side effects
      camera.powerOff();

    }, (memoryCard, sensor) -> { // TESTING

      context.checking(new Expectations() {{
        never(memoryCard);
        never(sensor);
      }});

      // shutter should do nothing
      camera.pressShutter();

    });
  }

  @Test
  public void pressingTheShutterWithThePowerOn_copiesTheDataFromTheSensorToTheMemoryCard() {
    defineTest(() -> { // SETUP

      // power on the camera to ensure the test performs
      // as intended without side effects
      camera.powerOn();

    }, (memoryCard, sensor) -> { // TESTING

      Sequence shutterSensorMemoryCardSequence = context.sequence(
          "shutterSensorMemoryCardSequence");

      // Example data that readData() could return
      final byte[] mockData = new byte[]{1, 2, 3, 4};

      context.checking(new Expectations() {{
        oneOf(sensor).readData();
        inSequence(shutterSensorMemoryCardSequence);
        will(returnValue(mockData));
        oneOf(memoryCard).write(with(equal(mockData)));
        inSequence(shutterSensorMemoryCardSequence);
      }});

      // now, shutter should copy data from the sensor to the memory card
      camera.pressShutter();

    });
  }

  /**
   * If data is currently being written, switching the camera off does not power down the sensor,
   * and then, once writing the data has completed, the camera powers down the sensor.
   */
  @Test
  public void ifDataIsCurrentlyBeingWritten_switchingTheCameraOffDoesNotPowerDownTheSensor() {
    defineTest(() -> { // SETUP

      // power on the camera to ensure the test performs
      // as intended without side effects
      camera.powerOn();

    }, (memoryCard, sensor) -> { // TESTING
      Sequence powerDownWhileWritingSequence = context.sequence("powerDownWhileWritingSequence");

      context.checking(new Expectations() {{
        oneOf(sensor).readData();
        inSequence(powerDownWhileWritingSequence);
        oneOf(memoryCard).write(with(any(byte[].class)));
        inSequence(powerDownWhileWritingSequence);

        oneOf(sensor).powerDown();
        inSequence(powerDownWhileWritingSequence);
      }});

      // shutter should copy data from the sensor to the memory card
      // expect 1x `sensor.readData()` and 1x `memoryCard.write(...)` for this
      camera.pressShutter();

      // while writing, should not trigger the sensor power down signal
      camera.powerOff();

      // but the camera should now be off
      assertThat(camera.isPoweredOn(), is(false));

      // send writing complete signal, which should shut down the sensor
      camera.writeComplete();
    });
  }
}
