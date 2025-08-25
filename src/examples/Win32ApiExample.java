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
	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 1;
	private static String lastErrorMessage;
	
	public static void main(String... args)
	{
		long hInstance = GetModuleHandle(null, (CharSequence) null);
		int exitCode = WinMain(hInstance, NULL, NULL, 0);
		
		if (exitCode == EXIT_FAILURE)
			System.err.println("Error: " + lastErrorMessage);
		
		System.exit(exitCode);
	}
	
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
		
		long window = CreateWindowEx(null, 0, className, MemoryUtil.memUTF16("Win32 API example"), WS_OVERLAPPEDWINDOW | WS_VISIBLE, CW_USEDEFAULT, CW_USEDEFAULT, 1280, 720, NULL, NULL, hInstance, NULL);
		
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
