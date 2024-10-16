package ic.doc.camera;

public class Camera implements WriteListener {

  private boolean isPoweredOn;
  private boolean isWriting;
  private boolean isWaitingToPowerDownSensor;
  private final MemoryCard memoryCard;
  private final Sensor sensor;

  public Camera(MemoryCard memoryCard, Sensor sensor) {
    isPoweredOn = false;
    isWriting = false;
    isWaitingToPowerDownSensor = false;
    this.memoryCard = memoryCard;
    this.sensor = sensor;

    // Send signal to sensor to power down,
    // in case it wasn't off already
    this.sensor.powerDown();
  }

  public boolean isPoweredOn() {
    return isPoweredOn;
  }

  public void pressShutter() {
    // If powered off, do nothing
    if (!isPoweredOn) {
      return;
    }

    // Otherwise, copy data from the sensor to the memory card.
    // Set `isWriting` flag, and wait to be notified of completion.
    isWriting = true;
    memoryCard.write(sensor.readData());
  }

  public void powerOn() {
    // If already powered on, do nothing.
    if (isPoweredOn) {
      return;
    }

    // Otherwise, power on.
    isPoweredOn = true;
    sensor.powerUp();
  }

  public void powerOff() {
    // If already powered off, do nothing.
    if (!isPoweredOn) {
      return;
    }

    // Otherwise, power off.
    isPoweredOn = false;

    // Don't power down sensor
    // if writing to memory card.
    if (isWriting) {
      isWaitingToPowerDownSensor = true;
    } else {
      sensor.powerDown();
    }
  }

  @Override
  public void writeComplete() {
    // Completion notification resets the `isWriting` flag
    isWriting = false;

    // If waiting to power down sensor
    // then power it down and reset the flag
    if (isWaitingToPowerDownSensor) {
      sensor.powerDown();
      isWaitingToPowerDownSensor = false;
    }
  }
}

