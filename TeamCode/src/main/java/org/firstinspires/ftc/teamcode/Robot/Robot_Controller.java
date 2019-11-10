package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Utils.Transform;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Robot_Controller {
    DcMotor rfm, lfm, rbm, lbm;
    private Robot_Localizer robot;
    public String telem;
    public Robot_Controller(DcMotor rfm, DcMotor lfm, DcMotor rbm, DcMotor lbm,  Robot_Localizer robot)
    {
        this.rfm = rfm;
        this.lfm = lfm;
        this.rbm = rbm;
        this.lbm = lbm;
        this.robot = robot;
    }
    public void setVec(Transform dir, double power)
    {
        dir.y *= -1;
        double sideMultiplierInverse                   = abs(dir.x + dir.y)+abs(dir.r);
        double sideMultiplier = min(sideMultiplierInverse, 1) / sideMultiplierInverse;

        lfm.setPower( (dir.y * sideMultiplier + dir.x * sideMultiplier - dir.r * sideMultiplier)*power );
        rfm.setPower((dir.y * sideMultiplier - dir.x * sideMultiplier + dir.r * sideMultiplier)*power );
        lbm.setPower(  (dir.y * sideMultiplier - dir.x * sideMultiplier - dir.r * sideMultiplier)*power );
        rbm.setPower( (dir.y * sideMultiplier + dir.x * sideMultiplier + dir.r * sideMultiplier)*power );
    }
    public void gotoPoint(Transform point)
    {
        Transform dir = new Transform(point.x-robot.pos.x,point.y-robot.pos.y,0);
        dir.normalize();
        dir.rotate(new Transform(0,0,0),-robot.pos.r);
        double goalDist = Math.hypot(robot.pos.x-point.x,robot.pos.y-point.y);
        double fPower = 1-(0.7/(0.00003*goalDist*goalDist));
        double rOffset = (Math.atan2(dir.y,dir.x)-((robot.pos.r%Math.PI)%-Math.PI))*fPower;
        double rPower = 1-(fPower/(1+0.5*rOffset*rOffset))*Math.signum(rOffset);
        dir.r = rPower;
        if(goalDist<50)
        {
            double turnToOffset = point.r-((robot.pos.r%(Math.PI*2))%-(Math.PI*2));
            double turnToMulti = 1-(0.7/(1+0.5*turnToOffset*turnToOffset))*Math.signum(turnToOffset);
            if(turnToOffset<0.03)setVec(new Transform(0,0,turnToMulti),1);
            else setVec(new Transform(0,0,0),0);
        }
        else
        {
            setVec(dir,1);
        }
    }
}