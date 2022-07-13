package screencapture4j;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.GDI32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ScreenCapture {

	public String windowName = "";
	
	public HWND hWnd = null;
	public BufferedImage image = null;
	


	
	public void setWindow(String windowName) {
		this.windowName = windowName;
	}
	
	public BufferedImage current_image() {
		this.hWnd = User32.INSTANCE.GetDesktopWindow();//User32.INSTANCE.FindWindow(null, this.windowName);

		this.image = this.capture(this.hWnd);
		return this.image;
	}

	public BufferedImage capture(HWND hWnd) {
        HDC hdcWindow = User32.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);
        RECT bounds = new RECT();
        screencapture4j.User32Extra.INSTANCE.GetClientRect(hWnd, bounds);
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;
        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);
        HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        screencapture4j.GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, screencapture4j.WinGDIExtra.SRCCOPY);
        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);
        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return image;
    }
	
	/**
	 * [param: type] formats: "jpg", "png", "gif"
	 * 
	 * @param fileName
	 * @param type 
	 */

}
