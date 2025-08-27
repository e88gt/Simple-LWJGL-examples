package examples;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.WinBase.*;

import java.nio.*;

import org.lwjgl.system.*;
import org.lwjgl.system.windows.*;

/**
 * 
 */
public class Win32ApiExample
{
	/* 
	 * these are for error handling, you can leave these
	 */
	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 1;
	private static String lastErrorMessage;
	
	/* 
	 * typical java main method
	 */
	public static void main(String... args)
	{
		long hInstance = GetModuleHandle(null, (CharSequence) null);
		int exitCode = WinMain(hInstance, NULL, NULL, 0);
		
		if (exitCode == EXIT_FAILURE)
			System.err.println("Error: " + lastErrorMessage);
		
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
	 * {@value #EXIT_FAILURE} if the execution was failed
	 * {@value #EXIT_SUCCESS} fi the execution was successful
	 */
	public static int WinMain(long hInstance, long hPrevInstance, long lpCmdLine, int nCmdShow)
	{
		ByteBuffer className = MemoryUtil.memUTF16("WinApiEx");
		
		WNDCLASSEX wc = WNDCLASSEX.calloc();
		wc.cbSize(WNDCLASSEX.SIZEOF);
		wc.lpfnWndProc(Win32ApiExample::WndProc);
		wc.hInstance(hInstance);
		wc.lpszClassName(className);
		
		if (RegisterClassEx(null, wc) == FALSE) {
			lastErrorMessage = "Failed to register window class";
			return EXIT_FAILURE;
		}
		
		long window = CreateWindowEx(null, 0, // we Can leave These to None
			className,
			MemoryUtil.memUTF16("Win32 API example"),
			WS_OVERLAPPEDWINDOW | WS_VISIBLE,
			CW_USEDEFAULT, CW_USEDEFAULT,
			1280, 720,
			NULL, NULL, hInstance, NULL);
		
		if (window == NULL) {
			lastErrorMessage = "Failed to create window";
			return EXIT_FAILURE;
		}
		
		MSG msg = MSG.create();
		while (GetMessage(null, msg, NULL, 0, 0)) {
			TranslateMessage(msg);
			DispatchMessage(msg);
		}
		
		if (!UnregisterClass(null, className, hInstance)) {
			lastErrorMessage = "Failed to unregister window class";
			return EXIT_FAILURE;
		}
		
		wc.free();
		msg.free();
		
		return EXIT_SUCCESS;
	}
	
	private static long WndProc(long hWnd, int uMsg, long wParam, long lParam)
	{
		if (uMsg == WM_CLOSE)
			DestroyWindow(null, hWnd);
		
		if (uMsg == WM_DESTROY)
			PostMessage(null, hWnd, WM_QUIT, wParam, lParam);
		
		return DefWindowProc(hWnd, uMsg, wParam, lParam);
	}
}
