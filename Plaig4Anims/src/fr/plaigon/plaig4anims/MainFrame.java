/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package fr.plaigon.plaig4anims;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import GLLoader.GLModel;

/**
 * Loads the Stanford bunny .obj file and draws it.
 *
 * @author Oskar Veerhoek
 */
public class MainFrame
{
	private static final String MODEL_LOCATION = "res/3d models/sakon.obj";
	private static GLModel sakonModel;
	
	private static Vector3f rotation = new Vector3f();
	private static Vector3f location = new Vector3f();
	
	public static void main(String[] args)
	{		
		setUpDisplay();
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(30f, (float) (640 / 480), 0.3f, 100f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		
		
		while (!Display.isCloseRequested())
		{
			render();
			checkInput();
			Display.update();
			Display.sync(60);
		}
		System.exit(0);
	}

	private static void setUpDisplay()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.setVSyncEnabled(true);
			Display.setResizable(true);
			Display.setTitle("Happy Easter!");
			Display.create();
		}
		catch (LWJGLException e)
		{
			System.err.println("The display wasn't initialized correctly. :(");
			Display.destroy();
			System.exit(1);
		}
	}
	
	private static void render()
	{
		if(sakonModel == null)
			sakonModel = new GLModel(MODEL_LOCATION);
		
		glPushMatrix();
			glEnable(GL_CULL_FACE);
		    glCullFace(GL_BACK);
			glEnable(GL_DEPTH_TEST);
        	glEnable(GL_ALPHA_TEST);
//        	glAlphaFunc(GL_GREATER, 0.5F);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glColor3f(1f, 1f, 1f);
			
			glEnable(GL_TEXTURE_2D);                     

//		    glShadeModel(GL_SMOOTH);
//		    glDepthFunc(GL_LESS);
//		    glDepthMask(true);
//		    glEnable(GL_NORMALIZE); 
		    //enable lighting
//		    glEnable(GL_LIGHTING);
			
			glRotatef(rotation.x, 1, 0, 0);
			glRotatef(rotation.y, 0, 1, 0);
			glRotatef(rotation.z, 0, 0, 1);
			glTranslatef(location.x, location.y, location.z);
	//		 glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			sakonModel.render();
		glPopMatrix();
	}
	
	private static void checkInput() 
	{
		boolean up = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean down = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean left = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean right = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_E);
		boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_Q);
		boolean speedUp = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean slowDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		float walkSpeed = 0.15F;
		
		float mx = Mouse.getDX();
		float my = Mouse.getDY();
		mx *= 0.25F;
		my *= 0.25F;
		rotation.y += mx;
		if(rotation.y > 360)
			rotation.y -= 360;
		
		rotation.x -= my;
		if(rotation.x > 85)
			rotation.x = 85;
		if(rotation.x < -85)
			rotation.x = -85;
		
		if(speedUp && !slowDown)
			walkSpeed = 0.25F;
		if(slowDown && !speedUp)
			walkSpeed = 0.10F;
		
		if(up && !down)
		{
			float cz  = (float) (walkSpeed * 2 * Math.cos(Math.toRadians(rotation.y)));
			float cx  = (float) (walkSpeed * Math.sin(Math.toRadians(rotation.y)));
			location.z += cz;
			location.x -= cx;
		}
		if(down && !up)
		{
			float cz  = (float) (walkSpeed * 2 * Math.cos(Math.toRadians(rotation.y)));
			float cx  = (float) (walkSpeed * Math.sin(Math.toRadians(rotation.y)));
			location.z -= cz;
			location.x += cx;
		}
		if(right && !left)
		{
			float cz  = (float) (walkSpeed * 2 * Math.cos(Math.toRadians(rotation.y + 90)));
			float cx  = (float) (walkSpeed * Math.sin(Math.toRadians(rotation.y)));
			location.z += cz;
			location.x -= cx;
		}
		if(left && !right)
		{
			float cz  = (float) (walkSpeed * 2 * Math.cos(Math.toRadians(rotation.y + 90)));
			float cx  = (float) (walkSpeed * Math.sin(Math.toRadians(rotation.y)));
			location.z -= cz;
			location.x += cx;
		}
		
		if(flyUp && !flyDown)
			location.y -= walkSpeed;
		if(flyDown && !flyUp)
			location.y += walkSpeed;
	}
}