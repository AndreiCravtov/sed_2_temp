package ic.doc.camera;

public class Camera {
  private boolean isPoweredOn;
  private final MemoryCard memoryCard;
  private final Sensor sensor;

  public Camera(MemoryCard memoryCard, Sensor sensor) {
    isPoweredOn = false;
    this.memoryCard = memoryCard;
    this.sensor = sensor;
  }

  public boolean isPoweredOn() {
    return isPoweredOn;
  }

  public void pressShutter() {
    // if powered off, do nothing
    if (!isPoweredOn) return;

    // otherwise, copy data from
    // the sensor to the memory card
    memoryCard.write(sensor.readData());
  }

  public void powerOn() {
    isPoweredOn = true;
    sensor.powerUp();
  }

  public void powerOff() {
    isPoweredOn = false;
    sensor.powerDown();
  }
}

