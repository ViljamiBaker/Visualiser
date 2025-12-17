package visualiser.Renderer.Util;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GUTILVB {
    public static Quaternionf eulerToQuaternion(Vector3f in){
        return eulerToQuaternion(in.x, in.y, in.z);
    }

    public static Quaternionf eulerToQuaternion(float pitch, float yaw, float roll){
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);

        double w = cr * cp * cy + sr * sp * sy;
        double x = sr * cp * cy - cr * sp * sy;
        double y = cr * sp * cy + sr * cp * sy;
        double z = cr * cp * sy - sr * sp * cy;
        return new Quaternionf(x, y, z, w);
    }

    public static Vector3f rotateXY(Vector3f in){
        float temp = in.x;
        in.x = in.z;
        in.z = temp;
        return in;
    }

    public static Vector3f eulerAngToVector3(Vector3f rotation){
        return eulerAngToVector3(rotation.z, rotation.y);
    }

    public static Vector3f eulerAngToVector3(float pitch, float yaw){
        float x = Math.cos(yaw) * Math.cos(pitch);
		float y = Math.sin(pitch);
		float z = Math.sin(yaw) * Math.cos(pitch);

        return new Vector3f(x,y,z);
    }

    public static float[] vector3toAngles(Vector3f in){
        float pitch = Math.asin(in.y);
        float yaw = Math.acos(in.x/Math.cos(pitch)) * Math.signum(-in.z);

        return new float[] {0,yaw,pitch};
    }

    public static Quaternionf rotateQuatByEuler(Quaternionf quat, float pitch, float yaw, float roll){
        return quat.add(eulerToQuaternion(pitch, yaw, roll));
    }

    public static Quaternionf rotateQuatByEuler(Quaternionf quat, float pitch, float yaw, float roll, Quaternionf dest){
        return quat.add(eulerToQuaternion(pitch, yaw, roll), dest);
    }

    public static boolean contains(Object[] arr, Object o){
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals(o)){
                return true;
            }
        }
        return false;
    }
}