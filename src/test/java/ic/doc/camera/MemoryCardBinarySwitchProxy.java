package ic.doc.camera;

import org.jmock.api.Expectation;

/**
 * A helper {@link MemoryCard} proxy for setting up the state of the {@link Camera} without
 * triggering any mocking {@link Expectation} by accident
 */
public class MemoryCardBinarySwitchProxy implements MemoryCard {

  private boolean isUsingFirstMemoryCard;
  private final MemoryCard firstMemoryCard;
  private final MemoryCard secondMemoryCard;

  public MemoryCardBinarySwitchProxy(MemoryCard firstMemoryCard, MemoryCard secondMemoryCard,
      boolean isUsingFirstMemoryCard) {
    this.isUsingFirstMemoryCard = isUsingFirstMemoryCard;
    this.firstMemoryCard = firstMemoryCard;
    this.secondMemoryCard = secondMemoryCard;
  }

  public MemoryCardBinarySwitchProxy(MemoryCard firstMemoryCard, MemoryCard secondMemoryCard) {
    this(firstMemoryCard, secondMemoryCard, true);
  }

  public boolean isUsingFirstMemoryCard() {
    return isUsingFirstMemoryCard;
  }

  public boolean isUsingSecondMemoryCard() {
    return !isUsingFirstMemoryCard;
  }

  public void useFirstMemoryCard() {
    isUsingFirstMemoryCard = true;
  }

  public void useSecondMemoryCard() {
    isUsingFirstMemoryCard = false;
  }

  public void switchMemoryCard() {
    isUsingFirstMemoryCard = !isUsingFirstMemoryCard;
  }

  @Override
  public void write(byte[] data) {
    if (isUsingFirstMemoryCard) {
      firstMemoryCard.write(data);
    } else {
      secondMemoryCard.write(data);
    }
  }
}
