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
    // not implemented
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

