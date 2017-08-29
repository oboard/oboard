package com.oboard.a;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import net.sourceforge.jeval.Evaluator;

public class ScriptDrawable extends Drawable {
    Paint mPaint = new Paint();
    String mScript = "";
    Bitmap mCacheBitmap;
    RectF rectF = new RectF(0, 0, 0, 0);;

    Context pc;

    public ScriptDrawable(Context c, String script) {
        pc = c;
        if (script != null) {
            if (script.contains(";"))
                mScript = script;
            else
                mScript = ">" + script + ";";
        }
//BitmapShader bitmapShader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        //mPaint.setAntiAlias(true);
        //mPaint.setShader(bitmapShader);
    }

    public void drawCache(String scr, RectF drawR) {
        //Script!!!
        String[] sp = scr.split(";");
        RectF drawF = drawR;
        float drawRadius = 0;
        Canvas c = new Canvas(mCacheBitmap);
        Paint p = new Paint();
        Paint.FontMetrics fm = p.getFontMetrics();
        p.setAntiAlias(true);

        for (int i = 0; i < sp.length; i++) {
            //value
            String[] ls;
            if (sp[i].contains(">"))
                ls = sp[i].split(">");
            else
                ls = new String[] {sp[i]};
            try {
                switch (ls[0].replace("\n", "").trim()) {

                        //坐标
                    case "x":
                        drawF.left = fd(ls[1]);
                        break;
                    case "y":
                        drawF.top = fd(ls[1]);
                        break;
                    case "x2":
                        drawF.right = fd(ls[1]);
                        break;
                    case "y2":
                        drawF.bottom = fd(ls[1]);
                        break;
                    case "r":
                        drawRadius = fd(ls[1]);
                        break;
                    case "left":
                        drawF.left = fd(ls[1]);
                        break;
                    case "top":
                        drawF.top = fd(ls[1]);
                        break;
                    case "right":
                        drawF.right = fd(ls[1]);
                        break;
                    case "bottom":
                        drawF.bottom = fd(ls[1]);
                        break;
                    case "rect":
                        drawF = new RectF(fd(ls[1]), fd(ls[2]), fd(ls[3]), fd(ls[4]));
                        break;

                        //数据
                    case "argb":
                        p.setARGB(Integer.valueOf(ls[1]), Integer.valueOf(ls[2]), Integer.valueOf(ls[3]), Integer.valueOf(ls[4]));
                        break;
                    case "rgb":
                        p.setARGB(255, Integer.valueOf(ls[1]), Integer.valueOf(ls[2]), Integer.valueOf(ls[3]));
                        break;
                    case "p":
                        switch (ls[1].trim()) {
                            case "0":
                                p.setStyle(Paint.Style.FILL);
                                break;
                            case "1":
                                p.setStyle(Paint.Style.STROKE);
                                break;
                            case "2":
                                p.setStyle(Paint.Style.FILL_AND_STROKE);
                                break;
                        }
                    case "s":
                        p.setTextSize(fd(ls[1]));
                        break;

                        //绘图
                    case "drawrect":
                        c.drawRect(drawF, p);
                        break;
                    case "drawcircle":
                        c.drawCircle(drawF.left, drawF.top, drawRadius, p);
                        break;
                    case "drawimage":
                        c.drawBitmap(upImageSize(BitmapFactory.decodeFile(ls[1]), (int)drawF.width(), (int)drawF.height()), drawF.left, drawF.top, p);
                        break;
                    case "translate":
                        c.translate(fd(ls[1]), fd(ls[2]));
                        break;
                    case "rotate":
                        if (ls.length >= 3) 
                            c.rotate(fd(ls[1]), fd(ls[2]), fd(ls[3]));
                        else
                            c.rotate(fd(ls[1]));
                        break;
                    case "scale":
                        if (ls.length < 3)
                            c.scale(fd(ls[1]), fd(ls[2]));
                        else
                            c.scale(fd(ls[1]), fd(ls[2]), fd(ls[3]), fd(ls[4]));
                        break;


                    case "out":
                        fm = p.getFontMetrics();
                        drawF.top = drawF.top + (float)Math.ceil(fm.descent - fm.ascent) + 2;
                        c.drawText(eval(ls[1]), drawF.left, drawF.top, p);
                    case "":
                        fm = p.getFontMetrics();
                        drawF.top = drawF.top + (float)Math.ceil(fm.descent - fm.ascent) + 2;
                        c.drawText(ls[1], drawF.left, drawF.top, p);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 相片按相框的比例动态缩放
     * @param 要缩放的图片
     * @param width 模板宽度
     * @param height 模板高度
     * @return
     */
    public static Bitmap upImageSize(Bitmap bmp, int width, int height) {
        if (bmp == null) return null;

        // 计算比例
        float scaleX = (float)width / bmp.getWidth();// 宽的比例
        float scaleY = (float)height / bmp.getHeight();// 高的比例
        //新的宽高
        int newW = 0, newH = 0;
        if (scaleX > scaleY) {
            newW = (int) (bmp.getWidth() * scaleX);
            newH = (int) (bmp.getHeight() * scaleX);
        } else if (scaleX <= scaleY) {
            newW = (int) (bmp.getWidth() * scaleY);
            newH = (int) (bmp.getHeight() * scaleY);
        }
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

    public float fd(String s) {
        try {
            return dip2px(pc, Float.valueOf(new Evaluator().evaluate(s)));
        } catch (Exception e) {
            return dip2px(pc, Float.valueOf(s));
        }
    }

    public String eval(String s) {
        try {
            return new Evaluator().evaluate(s);
        } catch (Exception e) {
            return s;
        }
    }

    public float dip2px(Context context, float dipValue) {
        return (dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
    }

    public float px2dip(Context context, float pxValue) {
        return (pxValue / context.getResources().getDisplayMetrics().density - 0.5f) ;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(left, top, right, bottom);
        mCacheBitmap = Bitmap.createBitmap((int)rectF.width(), (int)rectF.height(), Bitmap.Config.ARGB_8888);

        String s = mScript.replace("@w", String.valueOf(rectF.width()));
        s = s.replace("@h", String.valueOf(rectF.height()));
        drawCache(s, new RectF(left, top, right, bottom));
    }

    @Override  
    public void draw(Canvas canvas) {
        if (mCacheBitmap == null) return;

        canvas.drawBitmap(mCacheBitmap, 0, 0, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override  
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;  
    }
}
