package visualiser.Renderer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import visualiser.Data.BaseConnection;
import visualiser.Data.BaseNode;
import visualiser.Data.Organiser;
import visualiser.Data.Path;
import visualiser.Data.Pathfinder;
import visualiser.Renderer.Objects.Camera;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.RenderData;
import visualiser.Renderer.Objects.Shader;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Renderer.Physical.PhysicalNode;
import visualiser.Renderer.Physical.PhysicalOrganiser;
import visualiser.Renderer.Util.RendererStartData;
import visualiser.Renderer.Util.RendererUpdateType;

import static visualiser.Renderer.Util.LUTILVB.*;

//https://learnopengl.com/Lighting/Colors
public class Renderer {
	public static float deltaTime = 0.0f;	// Time between current frame and last frame
	public static Camera camera;
	public static long window;
	
    private static boolean wireframe = false;
	private static boolean lockMouse = true;
	private static boolean ffw = false;
	private static boolean run = true;
	private static float lastFrame = 0.0f; // Time of last frame
	private static ArrayList<Float> lineData = new ArrayList<>();

	private static Shader nodeShader;
	private static Shader lineShader;

	@SuppressWarnings("rawtypes")
	public static PhysicalOrganiser o;

	public static Path path = null;
	public static int pathindex = -1;
	public static BaseNode selectedNode1 = null;
	public static BaseNode selectedNode2 = null;

	private static ShadowData shadowData = new ShadowData();

	private static int updateIndex = 0;

    public static <T> void start(Organiser<T> o2){
		start(o2, new Function[0], -1, RendererUpdateType.UpdateAll, new ShadowData());
	}

    public static <T> void start(Organiser<T> o2, int updateCount, RendererUpdateType updateType){
		start(o2, new Function[0], updateCount, updateType, new ShadowData());
	}

    public static <T> void start(Organiser<T> o2, Function[] fns){
		start(o2, fns, -1, RendererUpdateType.UpdateAll, new ShadowData());
	}

	public static <T> void start(Organiser<T> o2, RendererStartData dat){
		start(o2, dat.fns(),dat.updateCount(),dat.updateType(),dat.shadow());
	}

	@SuppressWarnings("unchecked")
    public static <T> void start(Organiser<T> o2, Function[] fns, int updateCount, RendererUpdateType updateType, ShadowData shadow){
		if(o2 instanceof PhysicalOrganiser){
			o = (PhysicalOrganiser<T>)o2;
		}else{
			o = new PhysicalOrganiser<T>(o2);
		}
		o.resetNodePositions();
        init();

		glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER,GLFW_TRUE);
        window = createWindow(1200, 800, "Visual");
		glEnable(GL_DEPTH_TEST);  
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		enableDebug();


		camera = new Camera(window);
		camera.position = new Vector3f(0,0,1);
		camera.enableCameraMovement();
		
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if(action == GLFW_PRESS){
				PhysicalNode<T> lowestNode = null;
				Vector3f temp = new Vector3f();
				float lowestNodeDist = Float.POSITIVE_INFINITY;
				for (int i = 0; i < o.getNodeCount(); i++) {
					PhysicalNode<T> n = o.getP(i);
					Vector3f diff = n.pos.sub(camera.position,temp);
					float length = diff.lengthSquared();
					if(camera.front.angle(diff)<0.02&&length<lowestNodeDist){
						lowestNode = n;
						lowestNodeDist = length;
					}
				}
				if(button == GLFW_MOUSE_BUTTON_LEFT){
					selectedNode1 = lowestNode;
					if(lowestNode!=null)
						System.out.println(lowestNode.d);
				}
				
				if(button == GLFW_MOUSE_BUTTON_RIGHT){
					selectedNode2 = lowestNode;
					if(lowestNode!=null)
						System.out.println(lowestNode.d);
				}
				
				if(button == GLFW_MOUSE_BUTTON_MIDDLE&&lowestNode!=null)
					System.out.println(lowestNode.d);
				if(button == GLFW_MOUSE_BUTTON_4&&lowestNode!=null)
					System.out.println(lowestNode.connections.length);
				if(button != GLFW_MOUSE_BUTTON_LEFT && button != GLFW_MOUSE_BUTTON_RIGHT)return;
				if(selectedNode1 == null || selectedNode2 == null ){
					path=null;
					pathindex = -1;
					return;
				}
				path = Pathfinder.calcPath(o, selectedNode1, selectedNode2);
				pathindex=0;
				//if(button == GLFW_MOUSE_BUTTON_RIGHT)
				//	System.out.println(Arrays.toString(selectedNode.connections));
				//if(button == GLFW_MOUSE_BUTTON_MIDDLE&&selectedNode instanceof PhysicalNode){
				//	Vector3f pos = ((PhysicalNode<T>)selectedNode).pos;
				//	System.out.println("x: " + pos.x + ", y: " + pos.y + ", z: " + pos.z);
				//}
			}
		});

        // x,y,z
		float vertices[] = {
			//Tri1
            -1,-1, 0,
             1,-1, 0,
            -1, 1, 0,
			//Tri2
			 1, 1, 0,
			 1,-1, 0,
			 -1, 1, 0,
        };

		Matrix4f projection = new Matrix4f().perspective(1.0f, 300.0f / 300.0f, 0.1f, 100.0f);
		Matrix4f view = new Matrix4f();
		
		modelVAO = glGenVertexArrays();

		modelVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		glBindVertexArray(modelVAO);

		// pos
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*3, 0);
		glEnableVertexAttribArray(0);

		nodeShader = new Shader("shaderVertex", "shaderFrag");
		lineShader = new Shader("shaderLineVertex", "shaderLineFrag");

		setShadow(shadow);

        while(!glfwWindowShouldClose(window))
		{
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;  

			int[] width = new int[1];
			int[] height = new int[1];
			glfwGetWindowSize(window, width, height);
			float sr = (float)width[0]/(float)height[0];

			// input
			processInput(window);

			//double t1 = glfwGetTime();
			if(run){
				switch (updateType) {
					case UpdateAll:
					o.update((float)Math.min(deltaTime,0.05)*(ffw?30:1),glfwGetTime());
						break;
					case UpdateNumber:
					boolean wrap = o.update((float)Math.min(deltaTime,0.05)*(ffw?30:1),glfwGetTime(),updateIndex,updateIndex);
					if(wrap){
						updateIndex = 0;
					}else{
						updateIndex+=updateCount;
					}
						break;
					case UpdateToTime:
					updateIndex = o.update((float)Math.min(deltaTime,0.05)*(ffw?30:1),glfwGetTime(),updateIndex, updateCount, 0.01);
						break;
				
					default:
						break;
				}
			}

			if(camera.lockOn){
				if(path!=null){
					PhysicalNode<T> n = (PhysicalNode<T>)(path.nodes()[path.nodes().length-1-pathindex]);
					camera.lockOnPos = new Vector3f(n.pos);
				}else if(selectedNode1!=null){
					PhysicalNode<T> n = (PhysicalNode<T>)(selectedNode1);
					camera.lockOnPos = new Vector3f(n.pos);
				}
			}

			//System.out.println(o.getNodeCount() + " | " + (glfwGetTime()-t1));

			for (Function f : fns) {
				f.run();
			}

			//render code

			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			nodeShader.use();
			projection = camera.getProjection();
			nodeShader.setUniform("projection", projection);
			
			view = camera.getVeiw();

			nodeShader.setUniform("view", view);
			nodeShader.setFloat("sr", sr);

			nodeShader.use();

			/*CFrame[] cubePositions = GameLogic.getCubes();
			for (int i = 0; i < cubePositions.length; i++) {
				model = cubePositions[i].getAsMat4(); 
				lightingShader.setUniform("model", model);
				glBindVertexArray(cubeVertexArray);
				glDrawArrays(GL_TRIANGLES, 0, 36);
			}*/
			glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
			glBindVertexArray(modelVAO);
			if(!wireframe){
				if(path!=null){

					PhysicalNode<T> s = (PhysicalNode<T>)(path.nodes()[path.nodes().length-1-pathindex]);
					nodeShader.setUniform("pos", s.pos);
					nodeShader.setUniform("color", new Vector3f(0.75f,0.75f,0.0f));
					glDrawArrays(GL_TRIANGLES, 0, 6);

					for (BaseNode b : path.nodes()) {
						PhysicalNode<T> n = (PhysicalNode<T>)b;
						nodeShader.setUniform("pos", n.pos);
						nodeShader.setUniform("color", new Vector3f(0.75f,0.0f,0.0f));
						glDrawArrays(GL_TRIANGLES, 0, 6);
					}
				}
				for (int i = 0; i < o.getNodeCount(); i++) {
					PhysicalNode<T> n = o.getP(i);
					nodeShader.setUniform("pos", n.pos);
					if(n.equals(selectedNode1)||n.equals(selectedNode2)){
						nodeShader.setUniform("color", new Vector3f(0,1.0f,0));
					}else{
						if(n.d instanceof RenderData){
							nodeShader.setUniform("color", ((RenderData)n.d).color());
						}else{
							nodeShader.setUniform("color", new Vector3f(1.0f));
						}
					}
					glDrawArrays(GL_TRIANGLES, 0, 6);
				}
				nodeShader.setUniform("pos", new Vector3f());
				nodeShader.setUniform("color", new Vector3f(0.2f));
				glDrawArrays(GL_TRIANGLES, 0, 6);
			}
			if(path!=null){
				for (BaseConnection c : path.connections()) {
					PhysicalNode<T> n1 = (PhysicalNode<T>)c.n1;
					PhysicalNode<T> n2 = (PhysicalNode<T>)c.n2;
					drawLine(n1.pos, n2.pos, new Vector3f(0.0f, 0.75f,0.0f), new Vector3f(0.75f,0.0f,0.0f));
				}
			}
			for (BaseConnection c : o.connections) {
				PhysicalNode<T> n1 = (PhysicalNode<T>)c.n1;
				PhysicalNode<T> n2 = (PhysicalNode<T>)c.n2;
				if(c.bothway){
					if(
						n1.equals(selectedNode1)||n2.equals(selectedNode1)||
						n1.equals(selectedNode2)||n2.equals(selectedNode2)
					){
						drawLine(n1.pos, n2.pos, new Vector3f(0.0f,0.5f,0.0f));
					}else{
						drawLine(n1.pos, n2.pos, new Vector3f(0.5f));
					}
				}else{
					if(n1.equals(selectedNode1)||n1.equals(selectedNode2)){
						drawLine(n1.pos, n2.pos, new Vector3f(0.0f, 0.85f,0.0f), new Vector3f(0.75f,0.0f,0.75f));
					}else if(n2.equals(selectedNode1)||n2.equals(selectedNode2)){
						drawLine(n1.pos, n2.pos, new Vector3f(0.75f,0.75f,0.0f), new Vector3f(0.0f, 0.85f,0.0f));
					}else{
						drawLine(n1.pos, n2.pos, new Vector3f(0.5f,0.5f,0.0f), new Vector3f(0.5f,0.0f,0.5f));
					}
				}
			}
			Vector3f front = new Vector3f();
			camera.position.add(camera.front.mul(0.02f,new Vector3f()),front);
			drawLine(front, front.add(camera.right.mul(0.0004f,new Vector3f()),new Vector3f()), new Vector3f(0.0f,0.0f,1.0f));
			drawLine(front, front.add(camera.right.mul(-0.0004f,new Vector3f()),new Vector3f()), new Vector3f(0.0f,0.0f,1.0f));
			drawLine(front, front.add(camera.localup.mul(0.0004f,new Vector3f()),new Vector3f()), new Vector3f(0.0f,0.0f,1.0f));
			drawLine(front, front.add(camera.localup.mul(-0.0004f,new Vector3f()),new Vector3f()), new Vector3f(0.0f,0.0f,1.0f));

			//glDrawArrays(GL_TRIANGLES, 0, 36);
			
			//drawLine(new Vector3f(0.0f,0.0f,1.0f), new Vector3f(0.5f,0.5f,0.0f), new Vector3f(1.0f));
			//drawLine(new Vector3f(0,0,0), new Vector3f(0,1,0), new Vector3f(0,1,0));
			//drawLine(new Vector3f(0,0,0.1f), new Vector3f(0,0,1), new Vector3f(0,0,1));

			lineShader.use();
			lineShader.setUniform("projection", camera.getProjection());
			lineShader.setUniform("view", camera.getVeiw());
			drawLinesFlush();

			// check events and swap buffers
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		glfwTerminate();
    }

	public static void setShadow(ShadowData d){
		shadowData = d;
		lineShader.use();
		lineShader.setFloat("falloffRate", (float)shadowData.falloffRate);
		lineShader.setFloat("falloffDist", (float)shadowData.falloffDist);
		lineShader.setFloat("minFalloff", (float)shadowData.minFalloff);
		nodeShader.use();
		nodeShader.setFloat("falloffRate", (float)shadowData.falloffRate);
		nodeShader.setFloat("falloffDist", (float)shadowData.falloffDist);
		nodeShader.setFloat("minFalloff", (float)shadowData.minFalloff);
	}

	private static int lineVAO, lineVBO;

	static void drawLine(Vector3f p1, Vector3f p2, Vector3f color){
		drawLine(p1, p2, color, color);
	}

	static void drawLine(Vector3f p1, Vector3f p2, Vector3f color1, Vector3f color2)
	{
		// point 1
		lineData.add(p1.x);
		lineData.add(p1.y);
		lineData.add(p1.z);

		// color
		lineData.add(color1.x);
		lineData.add(color1.y);
		lineData.add(color1.z);

		// point 2
		lineData.add(p2.x);
		lineData.add(p2.y);
		lineData.add(p2.z);

		// color
		lineData.add(color2.x);
		lineData.add(color2.y);
		lineData.add(color2.z);
	}
	static void drawLine2d(Vector2f p1, Vector2f p2, Vector3f color)
	{
		drawLine(new Vector3f(p1, 0), new Vector3f(p2, 0), color);
	}

	private static boolean createdLineVAO = false;

	private static void drawLinesFlush()
	{
		float[] arr = new float[lineData.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = lineData.get(i);
		}

		if (!createdLineVAO)
		{
			createdLineVAO = true;

			lineVAO = glGenVertexArrays();

			lineVBO = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, lineVBO);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);

			glBindVertexArray(lineVAO);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*6,0);
			glEnableVertexAttribArray(0);

			glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES*6,Float.BYTES*3);
			glEnableVertexAttribArray(1);
		}
		else
		{
			glBindBuffer(GL_ARRAY_BUFFER, lineVBO);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);
		}

		// 6 floats make up a vertex (3 position 3 color)
		// divide by that to get number of vertices to draw
		int count = lineData.size()/6;

		glBindVertexArray(lineVAO);
		glDrawArrays(GL_LINES, 0, count);

		lineData.clear();
	}

	static int[] maxKey = {32,348,350,350};

	static boolean[] keysDown = new boolean[maxKey[maxKey.length-1]];
	static boolean[] keysDownLast = new boolean[maxKey[maxKey.length-1]];
	static boolean[] keysPressed = new boolean[maxKey[maxKey.length-1]];

	public static void updateKeysDown(long window){
		for (int x = 0; x < maxKey.length; x+=2) {
			for (int i = maxKey[x]; i < maxKey[x+1]; i++) {
				keysDown[i] = glfwGetKey(window, i) == GLFW_PRESS;
				keysPressed[i] = keysDown[i]&&!keysDownLast[i];
				keysDownLast[i] = glfwGetKey(window, i) == GLFW_PRESS;
			}
		}
	}

	// Returns if key is down (many times per press)
	public static boolean isKeyDown(int key){
		return keysDown[key];
	}
	// Returns if key was pressed last frame (once per press)
	public static boolean isKeyPressed(int key){
		return keysPressed[key];
	}

    public static void processInput(long window)
	{
		updateKeysDown(window);
		if(isKeyPressed(GLFW_KEY_ESCAPE))
			glfwSetWindowShouldClose(window, true);
		
		if(isKeyPressed(GLFW_KEY_P)){
			if(!wireframe){
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}else{
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
			wireframe = !wireframe;
		}
		if(isKeyPressed(GLFW_KEY_X))
			ffw = !ffw;
		if(isKeyPressed(GLFW_KEY_Y))
			run = !run;
		if(isKeyPressed(GLFW_KEY_K))
			camera.lockOn = !camera.lockOn;
		if(isKeyPressed(GLFW_KEY_L)){
			if(lockMouse){
				lockMouse = false;
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			}else{
				lockMouse = true;
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			}
			camera.setMouseMoveEnabled(lockMouse);
		}
		if(path!=null){
			if(Renderer.isKeyPressed(GLFW_KEY_LEFT)){
				pathindex--;
				pathindex = Math.clamp(pathindex, 0, path.nodes().length-1);
				System.out.println(path.nodes()[path.nodes().length-1-pathindex].d);
			}
			if(Renderer.isKeyPressed(GLFW_KEY_RIGHT)){
				pathindex++;
				pathindex = Math.clamp(pathindex, 0, path.nodes().length-1);
				System.out.println(path.nodes()[path.nodes().length-1-pathindex].d);
			}
		}
		
		camera.processInput(deltaTime);
	}

	private static int modelVAO, modelVBO;
}