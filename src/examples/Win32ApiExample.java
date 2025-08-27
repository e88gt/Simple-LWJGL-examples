package examples;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.WinBase.*;

import java.nio.*;
import java.util.*;

import org.lwjgl.system.*;
import org.lwjgl.system.windows.*;

/**
 * examples of class
 */
public class Win32ApiExample
{
	// these are for error handling, you can leave these
	
	/** error code for a successful run */
	public static final int EXIT_SUCCESS = 0;
	
	/** error code for failure */
	public static final int EXIT_FAILURE = 1;
	
	/** last error message */
	private static final ArrayList<String>errorMessages = new ArrayList<>();
	
	/**
	 * typical java main method
	 * 
	 * @param args <br>
	 * the command line arguments passed in<br>
	 * <br>
	 */
	public static void main(String... args)
	{
		// this is the instance of the current program running
		long hInstance = GetModuleHandle(null, (CharSequence) null);
		// we get the exit code, whether it was successful or not
		int exitCode = WinMain(hInstance, NULL, NULL, 0);
		
		// we print the error messages
		for (int i = 0; i < errorMessages.size(); i++) {
			String errorMessage = errorMessages.get(i);
			System.out.println("Error " + i + ": " + errorMessage);
		}
		
		// we exit using the exit code
		System.exit(exitCode);
	}
	
	/**
	 * the typical winmain function usually used for win32 api in c
	 * 
	 * @param hInstance <br>
	 * you can pass in {@link WinBase#GetModuleHandle(IntBuffer, CharSequence)}<br>
	 * <br>
	 * 
	 * @param hPrevInstance <br>
	 * you can pass in {@link MemoryUtil#NULL}<br>
	 * <br>
	 * 
	 * @param lpCmdLine <br>
	 * you can pass in {@link MemoryUtil#NULL}<br>
	 * <br>
	 * 
	 * @param nCmdShow <br>
	 * you can pass in 0<br>
	 * <br>
	 * 
	 * @return
	 * {@link #EXIT_FAILURE} or {@value #EXIT_FAILURE} if the execution was failed<br>
	 * {@link #EXIT_SUCCESS} or {@value #EXIT_SUCCESS} fi the execution was successful<br>
	 * <br>
	 */
	public static int WinMain(long hInstance, long hPrevInstance, long lpCmdLine, int nCmdShow)
	{
		// the name of the windows class
		ByteBuffer className = MemoryUtil.memUTF16("WinApiEx");
		
		// the window class extended
		WNDCLASSEX wc = WNDCLASSEX.calloc();
		// sets the size of the window
		// window is known for backwards compatibility
		// this cb size tells the window if we are using
		//   the old or new window class
		wc.cbSize(WNDCLASSEX.SIZEOF);
		// sets the window procedure or callback as we call it now
		// by setting this to the function pointer of WndProc (defined below)
		//   the window will call that function we set whenever an event happens
		wc.lpfnWndProc(Win32ApiExample::WndProc);
		// this is like telling the window which instance of the program we are
		//   on, for more detailed explanation, each windows software has a program
		//   and the program is like the current software running, but what you are
		//   actually looking at is the window, not the program, the program is what
		//   showing the window, and the window is just a visualizer i guess...
		wc.hInstance(hInstance);
		// this is the class name
		wc.lpszClassName(className);
		
		// we try to register class, if it fails, we return with exit failure code
		if (RegisterClassEx(null, wc) == FALSE) {
			errorMessages.add("Failed to register class");
			return EXIT_FAILURE;
		}
		
		// the window creation
		long window = CreateWindowEx(null, 0, // we Can leave These to None
			className, // the windows class name
			MemoryUtil.memUTF16("Win32 API example"), // the window title
			WS_OVERLAPPEDWINDOW | WS_VISIBLE, // the style of the window, WS here is Window Style
			CW_USEDEFAULT, CW_USEDEFAULT, // the window position, we use default
			1280, 720, // the width and the height for the window
			NULL, // the parent window
			NULL, // the menu
			hInstance, // the instance of the program
			NULL); // long pointer parameter
		
		// checking if the window failed to be created
		if (window == NULL) {
			errorMessages.add("Failed to create window");
			return EXIT_FAILURE;
		}
		
		// we create the message, this message thing is like a messenger, it sends you
		//   messages of what happened to the window
		MSG msg = MSG.create();
		// as long as there are still message then it will keep translating and
		//   dispatching messages
		while (GetMessage(null, // last error, null
			msg, // the message its gonna be getting
			NULL, // we leave this to null so that it gets messages from all windows
			0, 0)) { // these are just message 'min-max' filtering, we dont wanna filter messages
			
			TranslateMessage(msg); // translates the messages
			DispatchMessage(msg); // sends the message basically
		}
		
		// after a long time of the window running
		//   unless you are like me, u are gonna click the X button
		//   on the window eventually
		// so if we register class, we also have to unregister class
		// since registering classes uses memory, we need to free it
		if (!UnregisterClass(null, className, hInstance)) {
			errorMessages.add("Failed to unregister class");
			return EXIT_FAILURE;
		}
		
		// also freeing the window class and messages
		// in c you dont need to do this but we allocated
		//   it in heap earlier using calloc and we are in
		//   java anyways so have to free the allocated memories
		wc.free();
		msg.free();
		
		// we exit it successfully
		return EXIT_SUCCESS;
	}
	
	/**
	 * the window procedure<br>
	 * this procedure let you decide what the window is going to do when something happens to it<br>
	 * <br>
	 * 
	 * for example:<br>
	 * - if the window position changed, you can tell it to print 'position changed'<br>
	 * 
	 * @param hWnd <br>
	 * the current window that sent the message<br>
	 * <br>
	 * 
	 * @param uMsg <br>
	 * the message the window sent<br>
	 * <br>
	 * 
	 * @param wParam <br>
	 * the word (wchar) parameter<br>
	 * <br>
	 * 
	 * @param lParam <br>
	 * the long parameter<br>
	 * <br>
	 * 
	 * @return
	 * the window procedure<br>
	 * <br>
	 */
	private static long WndProc(long hWnd, int uMsg, long wParam, long lParam)
	{
		// when the X button on the window has been clicked
		if (uMsg == WM_CLOSE)
			// we destroy the window
			DestroyWindow(null, hWnd);
		
		// this was called by the DestroyWindow method
		if (uMsg == WM_DESTROY)
			// its supposed to be PostQuitMessage but we dont have that
			//   so we just post a WM_QUIT message
			PostMessage(null, hWnd, WM_QUIT, wParam, lParam);
		
		// returns the default window procedure
		return DefWindowProc(hWnd, uMsg, wParam, lParam);
	}
}
