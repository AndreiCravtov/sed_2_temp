package ic.doc.camera;

public class Camera {
  private final Sensor sensor;

  public Camera(Sensor sensor) {
    this.sensor = sensor;
  }

  public void pressShutter() {
    // not implemented
  }

  public void powerOn() {
    sensor.powerUp();
  }

  public void powerOff() {
    // not implemented
  }
}

