package ic.doc.camera;

import org.jmock.api.Expectation;

/**
 * A helper {@link Sensor} proxy for setting up the state of the {@link Camera}
 * without triggering any mocking {@link Expectation} by accident
 */
public class SensorBinarySwitchProxy implements Sensor {
  private boolean isUsingFirstSensor;
  private final Sensor firstSensor;
  private final Sensor secondSensor;

  public SensorBinarySwitchProxy(Sensor firstSensor, Sensor secondSensor, boolean isUsingFirstSensor) {
    this.isUsingFirstSensor = isUsingFirstSensor;
    this.firstSensor = firstSensor;
    this.secondSensor = secondSensor;
  }

  public SensorBinarySwitchProxy(Sensor firstSensor, Sensor secondSensor) {
    this(firstSensor, secondSensor, true);
  }

  public boolean isUsingFirstSensor() {
    return isUsingFirstSensor;
  }

  public boolean isUsingSecondSensor() {
    return !isUsingFirstSensor;
  }

  public void useFirstSensor() {
    isUsingFirstSensor = true;
  }

  public void useSecondSensor() {
    isUsingFirstSensor = false;
  }

  public void switchSensors() {
    isUsingFirstSensor = !isUsingFirstSensor;
  }

  @Override
  public byte[] readData() {
    if (isUsingFirstSensor) {
      return firstSensor.readData();
    } else {
      return secondSensor.readData();
    }
  }

  @Override
  public void powerUp() {
    if (isUsingFirstSensor) {
      firstSensor.powerUp();
    } else {
      secondSensor.powerUp();
    }
  }

  @Override
  public void powerDown() {
    if (isUsingFirstSensor) {
      firstSensor.powerDown();
    } else {
      secondSensor.powerDown();
    }
  }
}