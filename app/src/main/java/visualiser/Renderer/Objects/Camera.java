package visualiser.Renderer.Objects;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Util.GUTILVB;

public class Camera {
	public Vector3f position = new Vector3f(0.0f, 0.0f,  0.0f);
	public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	public Vector3f up = new Vector3f(0.0f, 1.0f,  0.0f);
	public Vector3f localup = new Vector3f(0.0f, 1.0f,  0.0f);
	public Vector3f right = new Vector3f(1.0f,0.0f,0.0f);
	public float fov = 1.0f;
	public float yaw = 0;
	public float pitch = 0;
	public boolean projectionGood = false;

	public boolean lockOn = false;
	public Vector3f lockOnPos = new Vector3f(1.0f,0.0f,0.0f);
	public float lockOnDist = 1;

	private float lastX = 400, lastY = 300;
	private boolean firstMouse = true;
    private long window;

	private Matrix4f lastProjection;

	boolean mouseMoveEnabled = true;
    public Camera(long window){
        this.window = window;
    }

	public void enableCameraMovement(){
		glfwSetCursorPosCallback(window, (windowInner, xpos, ypos)->{
			if(!mouseMoveEnabled)return;
			if (firstMouse) // initially set to true
			{
				lastX = (float)xpos;
				lastY = (float)ypos;
				firstMouse = false;
			}

			float xoffset = (float)xpos - lastX;
			float yoffset = lastY - (float)ypos;
			lastX = (float)xpos;
			lastY = (float)ypos;
			
			float sensitivity = 0.002f;
			xoffset *= sensitivity;
			yoffset *= sensitivity;

			yaw   += xoffset;
			pitch += yoffset;  

			if(pitch > 1.55f)
			pitch =  1.55f;
			if(pitch < -1.55f)
			pitch = -1.55f;

			front = GUTILVB.eulerAngToVector3(pitch, yaw);
			localup = GUTILVB.eulerAngToVector3(pitch+(float)Math.PI/2.0f, yaw);
			localup.cross(front,right);
		}); 
		glfwSetScrollCallback(window, (long window, double xoffset, double yoffset)->{
			lockOnDist *= 1.0f-0.2f*yoffset;
			lockOnDist = Math.clamp(lockOnDist, 0.01f, 100.0f);
		});
	}

    public Matrix4f getProjection(){
		if(projectionGood){
			return lastProjection;
		}
		projectionGood = true;
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(window, width, height);
		Matrix4f projection;
		if(!ortho){
			projection = new Matrix4f().perspective(fov, (float)width[0]/(float)height[0], 0.01f, 2000.0f);
		}else{
			projection =  new Matrix4f().ortho(-1, 1, -1, 1,0.01f, 100.0f);
		}
		lastProjection = projection;

		return projection;
    }

	public Matrix4f getVeiw(){
		if(lockOn){
			front.mul(-lockOnDist, position).add(lockOnPos);
			return new Matrix4f().lookAt(front.mul(-lockOnDist, new Vector3f()).add(lockOnPos), lockOnPos, up);
		}else{
			return new Matrix4f().lookAt(position, position.add(front, new Vector3f()), up);
		}
    }

	boolean ortho = false;
    public void processInput(float deltaTime){
		if(lockOn){
			return;
		}
		float cameraSpeed = (Renderer.isKeyDown(GLFW_KEY_LEFT_CONTROL)?15:5) * (Renderer.isKeyDown(GLFW_KEY_LEFT_SHIFT) ? 15: 2.5f) * deltaTime; // adjust accordingly
		if (Renderer.isKeyDown(GLFW_KEY_W)){
			position.add(front.mul(cameraSpeed, new Vector3f()));
		}
		if (Renderer.isKeyDown(GLFW_KEY_S)){
			position.sub(front.mul(cameraSpeed, new Vector3f()));
		}
		if (Renderer.isKeyDown(GLFW_KEY_A)){
			position.sub((front.cross(up, new Vector3f())).normalize().mul(cameraSpeed,new Vector3f()));
		}
		if (Renderer.isKeyDown(GLFW_KEY_D)){
			position.add((front.cross(up, new Vector3f())).normalize().mul(cameraSpeed,new Vector3f()));
		}
		if(Renderer.isKeyDown(GLFW_KEY_R)){
			fov += 0.05f;
			projectionGood = false;
		}
		if(Renderer.isKeyDown(GLFW_KEY_F)){
			fov -= 0.05f;
			projectionGood = false;
		}
		if(Renderer.isKeyPressed(GLFW_KEY_H)){
			ortho = !ortho;
			projectionGood = false;
			System.out.println(ortho);
		}
    }

	public boolean projectionGood(){
		return projectionGood;
	}
	public void setMouseMoveEnabled(boolean mouseMoveEnabled) {
		this.mouseMoveEnabled = mouseMoveEnabled;
		if(mouseMoveEnabled){
			firstMouse = true;
		}
	}
}
