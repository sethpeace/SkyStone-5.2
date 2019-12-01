package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Robot.Robot_Controller;
import org.firstinspires.ftc.teamcode.Robot.Robot_Localizer;
import org.firstinspires.ftc.teamcode.Utils.Interval;
import org.firstinspires.ftc.teamcode.Utils.Lambda;
import org.firstinspires.ftc.teamcode.Utils.Transform;
import org.firstinspires.ftc.teamcode.Utils.VuLambda;


@TeleOp(name="VuforiaTestDrive", group="Iterative Opmode")
//@Disabled
public class VuforiaTestDrive extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor center = null;
    private DcMotor right = null;
    private DcMotor left = null;

    private Robot_Localizer rowboat;
    private Robot_Controller control;

    private Interval inter;

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    private Servo collector_arm;
    private Servo foundation_mover;

    private CRServo outer_collector;
    private CRServo inner_collector;

    private VuforiaLocalizer vuforia;

    private WebcamName webcamName;

    private VuforiaTrackables skystoneTrackables;
    private VuforiaTrackable targetElement;

    private VuforiaTrackableDefaultListener block;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");

        collector_arm       = hardwareMap.get(Servo.class, "collector_arm");
        foundation_mover    = hardwareMap.get(Servo.class, "Foundation_mover");

        outer_collector     = hardwareMap.get(CRServo.class, "outer_collector");
        inner_collector     = hardwareMap.get(CRServo.class, "inner_collector");
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        rowboat = new Robot_Localizer(leftBack,rightFront,rightBack,0.958);
        control = new Robot_Controller(rightFront,leftFront,rightBack,leftBack,rowboat);

        //Vuforia init
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        //Seting vuforia params
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AXD7Z8n/////AAABmeUpWxkr4UwCn1T5SeLkoYsYLhZbVtkUUiH3anbbVLB6LppfJSGm+AVOaffZudIRjtBpgZG1MjRa4sz1YZPRUf/Tv9x0HQrm2+GfkHn2fi/Zu1GRH873rFjxnFnUIOar2q48nPytFs6n4/P4tkUMwBSmlffeJxcxhBSMnFgH5AXrTL7F+WAerdDGlFVGlHJgnbkMJWyFwsSrhkSm2TD2vnsiZ2PdnKhUPL3FLxHPTUh+b39PTlmW4Yzws1jDA+Xfp4lvn+E7p4g+fY/eAA3gzcRQP4XyhBYjACJaXOtatxclSNxBU5xyGN+L1cM5hQ/6d5UJBYQeQdV5GFzv0hd5xEYMCKcZplda+0y1f6+QG2Z6";
        parameters.cameraName = webcamName;
        parameters.addWebcamCalibrationFile("Calibration");


        //Loading assets
        this.vuforia = ClassFactory.getInstance().createVuforia(parameters);
        skystoneTrackables = this.vuforia.loadTrackablesFromAsset("Skystone");
        targetElement = skystoneTrackables.get(0);
        targetElement.setName("targetElement");


    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        collector_arm.setPosition(0.72);
        skystoneTrackables.activate();
        control.gotoPoint(new Transform(0,-320,0),true,true,0.25,(Object obj)->{
                detect(targetElement,(block)->{
                    OpenGLMatrix pose = block.getFtcCameraFromTarget();
                    telemetry.addData("test",pose);
                    telemetry.update();
                    control.gotoPoint(new Transform(-180-pose.getRow(0).get(3),this.rowboat.pos.y-pose.getRow(2).get(3)+200,0),true,true,0.2,(Object o)->{
                        collector_arm.setPosition(0.403);
                        inner_collector.setPower(-0.7);
                        outer_collector.setPower(-0.7);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        inner_collector.setPower(0);
                        outer_collector.setPower(0);

                        return 0;
                    });
                    //control.gotoPoint(new Transform(pose.getRow(0).get(3),200-pose.getRow(2).get(3),0),true);
                    return 0;
                });
                return 0;
        });

        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        rowboat.relocalize();
        /*OpenGLMatrix pose = getBlock(targetElement).getFtcCameraFromTarget();
        if(pose != null)telemetry.addData("x",pose.getRow(0).get(3));
        if(pose != null)telemetry.addData("y",pose.getRow(2).get(3)*0.9);*/
        //telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        if(inter != null) inter.clear();
    }
    private VuforiaTrackableDefaultListener getBlock(VuforiaTrackable trackable)
    {
        if(trackable != null)

        {
            VuforiaTrackable.Listener listener = trackable.getListener();
            if(listener instanceof VuforiaTrackableDefaultListener)
            {
                return ((VuforiaTrackableDefaultListener)listener);
            }
        }
        return null;
    }
    private void detect(VuforiaTrackable trackable, VuLambda callback)
    {
        inter = new Interval((obj)->{
            block = getBlock(targetElement);
            if(block.isVisible()){callback.call(block);return 1;}
            return 0;

        },500);
        telemetry.addData("test","test");
        telemetry.update();
        inter.start();
        telemetry.addData("test2","test2");
        telemetry.update();
    }

}
