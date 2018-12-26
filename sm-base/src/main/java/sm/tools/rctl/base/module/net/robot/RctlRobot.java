package sm.tools.rctl.base.module.net.robot;


import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;

public class RctlRobot {

    public static void main(String[] args) {
        Group group = new Group();
        Scene scene = new Scene(group, 1920, 1080);
        FXRobot robot = FXRobotFactory.createRobot(scene);
        robot.mouseMove(960,540);
        robot.mouseClick(MouseButton.SECONDARY);
    }
}
