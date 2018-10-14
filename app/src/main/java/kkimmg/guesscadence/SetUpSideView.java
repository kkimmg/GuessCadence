package kkimmg.guesscadence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 横から見た状態の電話機の位置、角度設定
 */
public class SetUpSideView extends View {
    /**
     * ビットマップを描画する
     */
    public static final Paint PAINT_BITMAP;
    /**
     * 電話機を描く線
     */
    public static final Paint PAINT_PHONE;
    /**
     * 電話機じゃない方を描く線
     */
    public static final Paint PAINT_NOT_PHONE;
    /**
     * 現在の電話機の角度
     */
    public static final Paint PAINT_CURRENT;

    /**
     * 線幅（電話）
     */
    public static final float STROKE_WDITH_PHONE = 10.0F;
    /**
     * 線幅（電話じゃない方）
     */
    public static final float STROKE_WDITH_NOT_PHONE = 5.0F;
    /**
     * 線幅（現在の電話機の状態）
     */
    public static final float STROKE_WDITH_CURRENT = 3.0F;
    /**
     * 電話機の高さ
     */
    public static final float PHONE_HALF_LENGTH = 50.0F;
    /**
     * 電話機の幅
     */
    public static final float PHONE_HALF_WIDTH = 30.0F;

    /**
     * 電話の位置設定の幅
     */
    public static final int RECT_WIDTH = 30;
    /**
     * ハンドルバー上に電話がある場合のX座標割合位置
     */
    private static final float PHONE_LOC_X_ON_BAR = 0.42F;
    /**
     * ハンドルバー上に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_BAR = 0.4F;
    /**
     * チューブトップ上に電話がある場合のX座標割合位置
     */
    private static final float PHONE_LOC_X_ON_TUBE = 0.55F;
    /**
     * チューブトップ上に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_TUBE = 0.65F;

    static {
        //電話機を描く線
        PAINT_BITMAP = new Paint();
        // 電話機を描く線
        PAINT_PHONE = new Paint();
        PAINT_PHONE.setColor(Color.RED);
        PAINT_PHONE.setStyle(Paint.Style.STROKE);
        PAINT_PHONE.setStrokeWidth(STROKE_WDITH_PHONE);
        // 電話機じゃない方を描く線
        PAINT_NOT_PHONE = new Paint();
        PAINT_NOT_PHONE.setColor(Color.BLUE);
        PAINT_NOT_PHONE.setStyle(Paint.Style.STROKE);
        PAINT_NOT_PHONE.setStrokeWidth(STROKE_WDITH_NOT_PHONE);
        // 現在の電話機の状態
        PAINT_CURRENT = new Paint();
        PAINT_CURRENT.setColor(Color.BLACK);
        PAINT_CURRENT.setStyle(Paint.Style.STROKE);
        PAINT_CURRENT.setStrokeWidth(STROKE_WDITH_CURRENT);
        PAINT_CURRENT.setTextSize(20.0F);

    }

    /**
     * 加速度
     */
    private float[] gravity = new float[3];
    /**
     * 電話の自転車への設置状況
     */
    private SetUpInfo setUpInfo;
    /**
     * 描画するビットマップ
     */
    private Bitmap bitmap;
    /**
     * 矢印　上
     */
    private Bitmap arrow_top;
    /**
     * 矢印　下
     */
    private Bitmap arrow_bottom;
    /**
     * 矢印　右
     */
    private Bitmap arrow_right;
    /**
     * 矢印　左
     */
    private Bitmap arrow_left;
    /**
     * 現在の電話機の角度
     */
    private float currentAngle = 0;

    /**
     * コンストラクタ
     */
    public SetUpSideView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * コンストラクタ
     */
    public SetUpSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * コンストラクタ
     */
    public SetUpSideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * ３点目は２点間を結ぶ線分の近くにあるか？
     *
     * @param point1 点１
     * @param point2 点２
     * @param point3 点３
     * @param r      基準とする距離
     * @return true/false 距離内/距離外
     */
    public static boolean isInRectangle(PointF point1, PointF point2, PointF point3, float r) {
        if (point1.y == point2.y) {
            if ((point1.x <= point3.x && point3.x <= point2.x) || (point2.x <= point3.x && point3.x <= point1.x)) {
                boolean w = r >= Math.abs(point1.y - point3.y);
                return w;
            }
            return false;
        }
        if (point1.x == point2.x) {
            if ((point1.y <= point3.y && point3.y <= point2.y) || (point2.y <= point3.y && point3.y <= point1.y)) {
                boolean w = r >= Math.abs(point1.x - point3.x);
                return w;
            }
            return false;
        }
        boolean ret = false;
        double slope = (double) (point2.y - point1.y) / (double) (point2.x - point1.x);
        double perpendicular = -(double) (point2.x - point1.x) / (double) (point2.y - point1.y);

        PointF point4 = new PointF();
        PointF point6 = new PointF();

        point4.y = point1.y - (float) (slope * point1.x);

        point6.x = (float) ((point3.y - point1.y + slope * point1.x - perpendicular * point3.x) / (slope - perpendicular));
        point6.y = (float) (perpendicular * point6.x) + point3.y - (int) (perpendicular * point3.x);

        float distanceX = point3.x - point6.x;
        float distanceY = point3.y - point6.y;
        double distanceP2 = Math.pow(distanceX, 2) + Math.pow(distanceY, 2);
        double r2 = Math.pow(r, 2);

        ret = (r2 >= distanceP2 ? true : false);
        if (ret) {
            ret &= (point1.x <= point6.x && point6.x <= point2.x) | (point2.x <= point6.x && point6.x <= point1.x);
            ret &= (point1.y <= point6.y && point6.y <= point2.y) | (point2.y <= point6.y && point6.y <= point1.y);
        }

        return ret;
    }

    /**
     * 加速度
     */
    public float[] getGravity() {
        return gravity;
    }

    /**
     * 加速度
     */
    public void setGravity(float[] gravity) {
        this.gravity = gravity;
    }

    /**
     * 中心の点から端点を取得する
     *
     * @param x      中心のX座標
     * @param y      中心のY座標
     * @param point1 端点1
     * @param point2 端点2
     * @param angle  角度
     */
    private void getPointFromCenter(float x, float y, PointF point1, PointF point2, double angle) {
        float x1, x2, y1, y2;
        x1 = x - (float) (SetUpSideView.PHONE_HALF_LENGTH * Math.abs(Math.cos(angle)));
        x2 = x + (float) (SetUpSideView.PHONE_HALF_LENGTH * Math.abs(Math.cos(angle)));
        y1 = y - (float) (SetUpSideView.PHONE_HALF_LENGTH * Math.abs(Math.sin(angle)));
        y2 = y + (float) (SetUpSideView.PHONE_HALF_LENGTH * Math.abs(Math.sin(angle)));

        point1.x = x1;
        point1.y = y1;
        point2.x = x2;
        point2.y = y2;
    }

    /**
     * 現在の電話機の角度
     *
     * @return 現在の電話機の角度
     */
    public float getCurrentAngle() {
        return currentAngle;
    }

    /**
     * 現在の電話機の角度
     *
     * @param currentAngle 現在の電話機の角度
     */
    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
        invalidate();
    }

    /**
     * 電話の自転車への設置状況
     */
    public SetUpInfo getSetUpInfo() {
        if (setUpInfo == null) setUpInfo = new SetUpInfo();
        return setUpInfo;
    }

    /**
     * 電話の自転車への設置状況
     */
    public void setSetUpInfo(SetUpInfo setUpInfo) {
        this.setUpInfo = setUpInfo;
    }

    /**
     * 共通の初期化部分
     */
    private void init(AttributeSet attrs, int defStyle) {
        /** 描画するビットマップ */
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.setup_side);
        /** 矢印　上 */
        arrow_top = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_top);
        /** 矢印　下 */
        arrow_bottom = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_bottom);
        /** 矢印　右 */
        arrow_right = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_right);
        /** 矢印　左 */
        arrow_left = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float zoom = (float) getBitmapZoom();
        if (bitmap != null) {
            Rect rectBitmap = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect rectAll = new Rect(0, 0, (int) (bitmap.getWidth() * zoom), (int) (bitmap.getHeight() * zoom));
            canvas.drawBitmap(bitmap, rectBitmap, rectAll, SetUpSideView.PAINT_BITMAP);
        }
        // 電話を描くよ
        drawPhone(canvas);
        // 矢印を描くよ
        drawArrows(canvas);
    }

    /**
     * 電話の位置をハンドルバー上として選択されたかの確認
     *
     * @param x     X座標
     * @param y     Y座標
     * @param angle 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneBarTop(float x, float y, double angle) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom);
        return isInPhoneArea(x, y, pcx, pcy, angle);
    }

    /**
     * 電話の位置をチューブトップ上として選択されたかの確認
     *
     * @param x     X座標
     * @param y     Y座標
     * @param angle 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneTubeTop(float x, float y, double angle) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom);
        return isInPhoneArea(x, y, pcx, pcy, angle);
    }

    /**
     * 電話が選択されたかの確認
     *
     * @param x     X座標
     * @param y     Y座標
     * @param cx    中心点のX座標
     * @param cy    中心点のY座標
     * @param angle 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneArea(float x, float y, float cx, float cy, double angle) {
        PointF point1 = new PointF(), point2 = new PointF();
        getPointFromCenter(cx, cy, point1, point2, angle);
        PointF point3 = new PointF(x, y);
        boolean ret = SetUpSideView.isInRectangle(point1, point2, point3, SetUpSideView.RECT_WIDTH);
        return ret;
    }

    /**
     * 電話を描画する
     */
    private void drawPhone(Canvas canvas) {
        float pcx, pcy, px1, px2, py1, py2;
        float ncx, ncy, nx1, nx2, ny1, ny2;
        float ccx, ccy, cx1, cx2, cy1, cy2;
        double radian = getSetUpInfo().getPitchByRadian() + SensorCache.HPI;
//        if (getGravity()[1] < 0) {
//            radian *= -1;
//        }
        double angle = Math.tan(radian);
        float zoom = (float) getBitmapZoom();
        double cos = -(Math.cos(radian));
        double sin = -(Math.sin(radian));
        double ccos = -(Math.cos(currentAngle + SensorCache.HPI));
        double csin = -(Math.sin(currentAngle + SensorCache.HPI));
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                px1 = pcx - (float) (SetUpSideView.PHONE_HALF_LENGTH * cos);
                px2 = pcx + (float) (SetUpSideView.PHONE_HALF_LENGTH * cos);
                py1 = pcy - (float) (SetUpSideView.PHONE_HALF_LENGTH * sin);
                py2 = pcy + (float) (SetUpSideView.PHONE_HALF_LENGTH * sin);
                canvas.drawLine(px1, py1, px2, py2, SetUpSideView.PAINT_PHONE);
                // 現在の角度
                ccx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                ccy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                px1 = ccx - (float) (SetUpSideView.PHONE_HALF_LENGTH * ccos);
                px2 = ccx + (float) (SetUpSideView.PHONE_HALF_LENGTH * ccos);
                py1 = ccy - (float) (SetUpSideView.PHONE_HALF_LENGTH * csin);
                py2 = ccy + (float) (SetUpSideView.PHONE_HALF_LENGTH * csin);
                canvas.drawLine(px1, py1, px2, py2, SetUpSideView.PAINT_CURRENT);
                // 電話じゃない方
                ncx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                ncy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                nx1 = ncx - SetUpSideView.PHONE_HALF_LENGTH;
                nx2 = ncx + SetUpSideView.PHONE_HALF_LENGTH;
                ny1 = ncy;
                ny2 = ncy;
                canvas.drawLine(nx1, ny1, nx2, ny2, SetUpSideView.PAINT_NOT_PHONE);
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                px1 = pcx - (float) (SetUpSideView.PHONE_HALF_LENGTH * cos);
                px2 = pcx + (float) (SetUpSideView.PHONE_HALF_LENGTH * cos);
                py1 = pcy - (float) (SetUpSideView.PHONE_HALF_LENGTH * sin);
                py2 = pcy + (float) (SetUpSideView.PHONE_HALF_LENGTH * sin);
                canvas.drawLine(px1, py1, px2, py2, SetUpSideView.PAINT_PHONE);
                // 現在の角度
                ccx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                ccy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                px1 = ccx - (float) (SetUpSideView.PHONE_HALF_LENGTH * ccos);
                px2 = ccx + (float) (SetUpSideView.PHONE_HALF_LENGTH * ccos);
                py1 = ccy - (float) (SetUpSideView.PHONE_HALF_LENGTH * csin);
                py2 = ccy + (float) (SetUpSideView.PHONE_HALF_LENGTH * csin);
                canvas.drawLine(px1, py1, px2, py2, SetUpSideView.PAINT_CURRENT);
                // 電話じゃない方
                ncx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                ncy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                nx1 = ncx - SetUpSideView.PHONE_HALF_LENGTH;
                nx2 = ncx + SetUpSideView.PHONE_HALF_LENGTH;
                ny1 = ncy;
                ny2 = ncy;
                canvas.drawLine(nx1, ny1, nx2, ny2, SetUpSideView.PAINT_NOT_PHONE);
                break;
        }
        canvas.drawText("ラジアン：" + String.valueOf(radian), 450, 50, PAINT_CURRENT);
        canvas.drawText("角度：" + String.valueOf((radian) * 180 / Math.PI), 450, 100, PAINT_CURRENT);
        canvas.drawText("ラジアン：" + String.valueOf(currentAngle + Math.PI / 2), 50, 50, PAINT_CURRENT);
        canvas.drawText("角度：" + String.valueOf((currentAngle + Math.PI / 2) * 180 / Math.PI), 50, 100, PAINT_CURRENT);
        canvas.drawText(String.valueOf(gravity[0]), 50, 150, PAINT_CURRENT);
        canvas.drawText(String.valueOf(gravity[1]), 50, 200, PAINT_CURRENT);
        canvas.drawText(String.valueOf(gravity[2]), 50, 250, PAINT_CURRENT);
        canvas.drawText("ラジアン：" + String.valueOf(getSetUpInfo().getPitchByRadian()), 450, 150, PAINT_CURRENT);
        canvas.drawText("角度：" + String.valueOf((getSetUpInfo().getPitchByRadian()) * 180 / Math.PI), 450, 200, PAINT_CURRENT);
    }

    /**
     * 矢印を描画する
     */
    private void drawArrows(Canvas canvas) {
        float pcx, pcy;
        float zoom = (float) getBitmapZoom();
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                drawArrows(canvas, pcx, pcy);
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                drawArrows(canvas, pcx, pcy);
                break;
        }
    }

    /**
     * 矢印を描画する
     */
    private void drawArrows(Canvas canvas, float pcx, float pcy) {
        // 上
        canvas.drawBitmap(arrow_top, new Rect(0, 0, arrow_top.getWidth(), arrow_top.getHeight())
                , new Rect((int) (pcx - arrow_top.getWidth() / 2),
                        (int) (pcy - SetUpSideView.PHONE_HALF_LENGTH - arrow_top.getHeight()),
                        (int) (pcx + arrow_top.getWidth() / 2),
                        (int) (pcy - SetUpSideView.PHONE_HALF_LENGTH)), SetUpSideView.PAINT_BITMAP);
        // 下
        canvas.drawBitmap(arrow_bottom, new Rect(0, 0, arrow_bottom.getWidth(), arrow_bottom.getHeight())
                , new Rect((int) (pcx - arrow_bottom.getWidth() / 2),
                        (int) (pcy + SetUpSideView.PHONE_HALF_LENGTH),
                        (int) (pcx + arrow_bottom.getWidth() / 2),
                        (int) (pcy + SetUpSideView.PHONE_HALF_LENGTH + arrow_bottom.getHeight())), SetUpSideView.PAINT_BITMAP);
        // 右
        canvas.drawBitmap(arrow_right, new Rect(0, 0, arrow_right.getWidth(), arrow_right.getHeight())
                , new Rect((int) (pcx + SetUpSideView.PHONE_HALF_LENGTH),
                        (int) (pcy - arrow_right.getHeight() / 2),
                        (int) (pcx + SetUpSideView.PHONE_HALF_LENGTH + arrow_right.getWidth()),
                        (int) (pcy + arrow_right.getHeight() / 2)), SetUpSideView.PAINT_BITMAP);
        // 左
        canvas.drawBitmap(arrow_left, new Rect(0, 0, arrow_left.getWidth(), arrow_left.getHeight())
                , new Rect((int) (pcx - SetUpSideView.PHONE_HALF_LENGTH - arrow_right.getWidth()),
                        (int) (pcy - arrow_right.getHeight() / 2),
                        (int) (pcx - SetUpSideView.PHONE_HALF_LENGTH),
                        (int) (pcy + arrow_right.getHeight() / 2)), SetUpSideView.PAINT_BITMAP);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        float angle = getSetUpInfo().getPitchByRadian();
        float offset = 1.0F;
        switch (getSetUpInfo().getPitchUnit()) {
            case SetUpInfo.UNIT_PERCENT:
                offset = (float) (Math.atan(0.01));
                break;
            case SetUpInfo.UNIT_PERMIL:
                offset = (float) (Math.atan(0.001));
                break;
            case SetUpInfo.UNIT_RADIAN:
            case SetUpInfo.UNIT_DEGLEE:
            default:
                offset = (float) (180.0D / Math.PI);
                break;
        }
        if (isInArrowTopButton(x, y)) {
            getSetUpInfo().setPitchByRadian(angle - offset);
            invalidate();
        } else if (isInArrowBottomButton(x, y)) {
            getSetUpInfo().setPitchByRadian(angle + offset);
            invalidate();
        } else if (isInArrowLeftButton(x, y)) {
            getSetUpInfo().setPitchByRadian(angle - offset);
            invalidate();
        } else if (isInArrowRightButton(x, y)) {
            getSetUpInfo().setPitchByRadian(angle + offset);
            invalidate();
        } else if (isInPhoneBarTop(x, y, angle)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_BAR_RIGHT);
            invalidate();
        } else if (isInPhoneTubeTop(x, y, angle)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_TUBETOP);
            invalidate();
        }

        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param x X座標
     * @param y Y座標
     * @return 入っている？
     */
    private boolean isInArrowTopButton(float x, float y) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowTopButton(pcx, pcy, x, y);
        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param x X座標
     * @param y Y座標
     * @return 入っている？
     */
    private boolean isInArrowBottomButton(float x, float y) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowBottomButton(pcx, pcy, x, y);
        return ret;
    }

    /**
     * 右の矢印ボタン内か？
     *
     * @param x X座標
     * @param y Y座標
     * @return 入っている？
     */
    private boolean isInArrowRightButton(float x, float y) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowRightButton(pcx, pcy, x, y);
        return ret;
    }

    /**
     * 左の矢印ボタン内か？
     *
     * @param x X座標
     * @param y Y座標
     * @return 入っている？
     */
    private boolean isInArrowLeftButton(float x, float y) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
            case SetUpInfo.SETUP_BAR_CENTER:
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowLeftButton(pcx, pcy, x, y);
        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param pcx 中心点のpcx
     * @param pcy 中心点のpcy
     * @param x   X座標
     * @param y   Y座標
     * @return 入っている？
     */
    private boolean isInArrowTopButton(float pcx, float pcy, float x, float y) {
        boolean ret = true;
        float x1 = (float) (pcx - arrow_top.getWidth() / 2);
        float y1 = (float) (pcy - SetUpSideView.PHONE_HALF_LENGTH - arrow_top.getHeight());
        float x2 = (float) (pcx + arrow_top.getWidth() / 2);
        float y2 = (float) (pcy - SetUpSideView.PHONE_HALF_LENGTH);

        ret &= x >= x1;
        ret &= x <= x2;
        ret &= y >= y1;
        ret &= y <= y2;

        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param pcx 中心点のpcx
     * @param pcy 中心点のpcy
     * @param x   X座標
     * @param y   Y座標
     * @return 入っている？
     */
    private boolean isInArrowBottomButton(float pcx, float pcy, float x, float y) {
        boolean ret = true;
        float x1 = (float) (pcx - arrow_bottom.getWidth() / 2);
        float y1 = (float) (pcy + SetUpSideView.PHONE_HALF_LENGTH);
        float x2 = (float) (pcx + arrow_bottom.getWidth() / 2);
        float y2 = (float) (pcy + SetUpSideView.PHONE_HALF_LENGTH + arrow_bottom.getHeight());

        ret &= x >= x1;
        ret &= x <= x2;
        ret &= y >= y1;
        ret &= y <= y2;

        return ret;
    }

    /**
     * 右の矢印ボタン内か？
     *
     * @param pcx 中心点のpcx
     * @param pcy 中心点のpcy
     * @param x   X座標
     * @param y   Y座標
     * @return 入っている？
     */
    private boolean isInArrowRightButton(float pcx, float pcy, float x, float y) {
        boolean ret = true;
        float x1 = (float) (pcx + SetUpSideView.PHONE_HALF_LENGTH);
        float y1 = (float) (pcy - arrow_right.getHeight() / 2);
        float x2 = (float) (pcx + SetUpSideView.PHONE_HALF_LENGTH + arrow_right.getWidth());
        float y2 = (float) (pcy + arrow_right.getHeight() / 2);

        ret &= x >= x1;
        ret &= x <= x2;
        ret &= y >= y1;
        ret &= y <= y2;

        return ret;
    }

    /**
     * 左の矢印ボタン内か？
     *
     * @param pcx 中心点のpcx
     * @param pcy 中心点のpcy
     * @param x   X座標
     * @param y   Y座標
     * @return 入っている？
     */
    private boolean isInArrowLeftButton(float pcx, float pcy, float x, float y) {
        boolean ret = true;
        float x1 = (float) (pcx - SetUpSideView.PHONE_HALF_LENGTH - arrow_right.getWidth());
        float y1 = (float) (pcy - arrow_right.getHeight() / 2);
        float x2 = (float) (pcx - SetUpSideView.PHONE_HALF_LENGTH);
        float y2 = (float) (pcy + arrow_right.getHeight() / 2);

        ret &= x >= x1;
        ret &= x <= x2;
        ret &= y >= y1;
        ret &= y <= y2;

        return ret;
    }

    /**
     * ビットマップの拡大／縮小率<br>
     * 縦横で大きさの比を出して、小さい方を返す
     *
     * @return 拡大率
     */
    private double getBitmapZoom() {
        double ret = 1.0D;
        if (bitmap != null) {
            double heightZoom = (double) getHeight() / (double) bitmap.getHeight();
            double widthZoom = (double) getWidth() / (double) bitmap.getWidth();
            ret = (heightZoom < widthZoom ? heightZoom : widthZoom);
        }
        return ret;
    }
}
