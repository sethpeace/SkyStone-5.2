/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.OldOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.Robot_Localizer;

import static java.lang.Math.abs;
import static java.lang.Math.min;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Movement Control", group="Linear Opmode")
@Disabled
@Deprecated
public class Movement_Control extends LinearOpMode {

    // Declare OpMode members.
    ElapsedTime runtime = new ElapsedTime();
    DcMotor leftFront;
    DcMotor rightFront;
    DcMotor leftBack;
    DcMotor rightBack;
    float sideMultiplier;
    float sideMultiplierInverse;
    double speedMultiplier;
    Robot_Localizer rowboat;
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        rowboat = new Robot_Localizer(leftBack,rightFront,rightBack,0.958);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

            //Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            rowboat.relocalize();
            //Defining variables to multiply by to scale motor power
            sideMultiplierInverse                   = abs(gamepad1.left_stick_x) + abs(gamepad1.left_stick_y) + abs(gamepad1.right_stick_x);
            sideMultiplier = min(sideMultiplierInverse, 1) / sideMultiplierInverse;
            if(gamepad1.left_bumper) speedMultiplier = 0.25;
            else                     speedMultiplier = 1;

            if(gamepad1.right_stick_x==0||true)
            {
                // Make the motors move, using the aforementioned variables.
                leftFront.setPower( (gamepad1.left_stick_y * sideMultiplier - gamepad1.left_stick_x * sideMultiplier + gamepad1.right_stick_x * sideMultiplier) * speedMultiplier);
                rightFront.setPower((gamepad1.left_stick_y * sideMultiplier + gamepad1.left_stick_x * sideMultiplier - gamepad1.right_stick_x * sideMultiplier) * speedMultiplier);
                leftBack.setPower(  (gamepad1.left_stick_y * sideMultiplier + gamepad1.left_stick_x * sideMultiplier + gamepad1.right_stick_x * sideMultiplier) * speedMultiplier);
                rightBack.setPower( (gamepad1.left_stick_y * sideMultiplier - gamepad1.left_stick_x * sideMultiplier - gamepad1.right_stick_x * sideMultiplier) * speedMultiplier);
            }/*
            else
            {
                leftFront.setPower(  gamepad1.right_stick_x * speedMultiplier);
                leftBack.setPower(   gamepad1.right_stick_x * speedMultiplier);
                rightFront.setPower(-gamepad1.right_stick_x * speedMultiplier);
                rightBack.setPower( -gamepad1.right_stick_x * speedMultiplier);
            }*/

            telemetry.addData("Status", "Running");
            telemetry.addData("r",((rowboat.pos.r%(Math.PI*2))%-(Math.PI*2)));
            telemetry.addData("x",rowboat.pos.x);
            telemetry.addData("y",rowboat.pos.y);
            telemetry.addData("telemA",rowboat.telemetryA);
            telemetry.addData("telemB",rowboat.telemetryB);
            telemetry.addData("telemC",rowboat.telemetryC);
            telemetry.update();
        }
    }
}
