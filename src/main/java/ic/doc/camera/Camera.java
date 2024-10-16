package ic.doc.camera;

public class Camera implements WriteListener {
  private boolean isPoweredOn;
  private boolean isWriting;
  private final MemoryCard memoryCard;
  private final Sensor sensor;

  public Camera(MemoryCard memoryCard, Sensor sensor) {
    isPoweredOn = false;
    isWriting = false;
    this.memoryCard = memoryCard;
    this.sensor = sensor;

    // Send signal to sensor to power down,
    // in case it wasn't off already
    this.sensor.powerDown();
  }

  public boolean isPoweredOn() {
    return isPoweredOn;
  }

  public boolean isWriting() {
    return isWriting;
  }

  public void pressShutter() {
    // If powered off, do nothing
    if (!isPoweredOn) return;

    // Otherwise, copy data from the sensor to the memory card.
    // Set `isWriting` flag, and wait to be notified of completion.
    isWriting = true;
    memoryCard.write(sensor.readData());
  }

  public void powerOn() {
    isPoweredOn = true;
    sensor.powerUp();
  }

  public void powerOff() {
    // Don't power off if writing to memory card
    if (isWriting) return;

    // Otherwise power off
    isPoweredOn = false;
    sensor.powerDown();
  }

  @Override
  public void writeComplete() {
    // Completion notification resets the `isWriting` flag
    isWriting = false;
  }
}

