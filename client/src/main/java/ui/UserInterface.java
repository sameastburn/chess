package ui;

public class UserInterface {
  private static final UserInterface instance = new UserInterface();

  public static UserInterface getInstance() {
    return instance;
  }

  public void draw() {
    System.out.println("UserInterface.draw");
  }
}
