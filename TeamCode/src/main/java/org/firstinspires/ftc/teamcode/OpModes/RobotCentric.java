package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.*;
import org.firstinspires.ftc.teamcode.Utils.Transform;


@TeleOp(name="Robot-Centric Driving", group="Iterative Opmode")
//@Disabled
public class RobotCentric extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private Robot_Localizer rowboat;
    private Robot_Controller control;

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;
    private DcMotor horizontal_extender;
    private DcMotor vertical_extender;

    private double gp1_percent_pwr;
    private double gp2_percent_pwr;

    private Transform saved_robot_pos;
    private Transform robot_vector;

    private boolean going_to_pt;

    @Override
    public void init() {
        leftFront           = hardwareMap.get(DcMotor.class, "left_front");
        rightFront          = hardwareMap.get(DcMotor.class, "right_front");
        leftBack            = hardwareMap.get(DcMotor.class, "left_back");
        rightBack           = hardwareMap.get(DcMotor.class, "right_back");
        horizontal_extender = hardwareMap.get(DcMotor.class, "horizontal_ext");
        vertical_extender   = hardwareMap.get(DcMotor.class, "vertical_ext");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        rowboat = new Robot_Localizer(leftBack,rightFront,rightBack,0.958);
        control = new Robot_Controller(rightFront,leftFront,rightBack,leftBack,rowboat);

        going_to_pt = false;
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        rowboat.relocalize();
        robot_vector = new Transform(gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x);

        if(gamepad1.left_bumper) gp1_percent_pwr = 0.25;
        else gp1_percent_pwr = 1;

        if(gamepad2.left_bumper) gp2_percent_pwr = 0.25;
        else gp2_percent_pwr = 1;

        if(gamepad1.y) saved_robot_pos = rowboat.pos.clone();
        if(gamepad1.x && saved_robot_pos != null && !going_to_pt)
        {
            going_to_pt = true;
            control.gotoPoint(saved_robot_pos, true, true,0.2, (Object obj)->{going_to_pt = false; return 0;});
        }

        if(gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0 || gamepad1.right_stick_x != 0 || gamepad1.right_stick_y != 0) {
            going_to_pt = false;
            control.clearGoto();
        }

        if(!going_to_pt) control.setVec(robot_vector, gp1_percent_pwr);

        horizontal_extender.setPower(-gamepad2.left_stick_y * gp2_percent_pwr);

        vertical_extender.setPower(gamepad2.right_stick_y * gp2_percent_pwr);

        telemetry.addData("r",rowboat.pos.r);
        telemetry.addData("x",rowboat.pos.x);
        telemetry.addData("y",rowboat.pos.y);
        telemetry.update();
    }

    @Override
    public void stop() {
    }
}