package ic.doc.camera;

public class Camera {
  private final MemoryCard memoryCard;
  private final Sensor sensor;

  public Camera(MemoryCard memoryCard, Sensor sensor) {
    this.memoryCard = memoryCard;
    this.sensor = sensor;
  }

  public void pressShutter() {
    // not implemented
  }

  public void powerOn() {
    sensor.powerUp();
  }

  public void powerOff() {
    sensor.powerDown();
  }
}

