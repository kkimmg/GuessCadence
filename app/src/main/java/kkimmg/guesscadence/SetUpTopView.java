package kkimmg.guesscadence;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 上から見た状態の電話機の位置、角度設定
 */
public class SetUpTopView extends View {
    /**
     * 選択されていない
     */
    private static final int LOCATION_NOTHING = -1;
    /**
     * ハンドルバー上に電話がある場合のX座標割合位置
     */
    private static final float PHONE_LOC_X_ON_BAR = 0.22F;
    /**
     * ハンドルバー右に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_BAR_RIGHT = 0.3F;
    /**
     * ハンドルバー中央に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_BAR_CENTER = 0.5F;
    /**
     * ハンドルバー左に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_BAR_LEFT = 0.7F;
    /**
     * チューブトップ上に電話がある場合のX座標割合位置
     */
    private static final float PHONE_LOC_X_ON_TUBE = 0.45F;
    /**
     * チューブトップ上に電話がある場合のY座標割合位置
     */
    private static final float PHONE_LOC_Y_ON_TUBE = 0.5F;
    /**
     * 変異量
     */
    private static final int OFFSET = 90;
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
     * コンストラクタ
     */
    public SetUpTopView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * コンストラクタ
     */
    public SetUpTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * コンストラクタ
     */
    public SetUpTopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
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
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.setup_over);
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
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneBarTopCenter(float x, float y, int rotation) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom);
        return isInPhoneArea(x, y, pcx, pcy, rotation);
    }

    /**
     * 電話の位置をハンドルバー上として選択されたかの確認
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneBarTopLeft(float x, float y, int rotation) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom);
        return isInPhoneArea(x, y, pcx, pcy, rotation);
    }

    /**
     * 電話の位置をハンドルバー上として選択されたかの確認
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneBarTopRight(float x, float y, int rotation) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom);
        return isInPhoneArea(x, y, pcx, pcy, rotation);
    }

    /**
     * 電話の位置をチューブトップ上として選択されたかの確認
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneTubeTop(float x, float y, int rotation) {
        float zoom = (float) getBitmapZoom();
        float pcx = (float) (bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom);
        float pcy = (float) (bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom);
        return isInPhoneArea(x, y, pcx, pcy, rotation);
    }

    /**
     * 電話が選択されたかの確認
     *
     * @param x        X座標
     * @param y        Y座標
     * @param cx       中心点のX座標
     * @param cy       中心点のY座標
     * @param rotation 角度
     * @return 入っているかどうか
     */
    private boolean isInPhoneArea(float x, float y, float cx, float cy, int rotation) {
        boolean ret = true;
        RectF prect = getPhoneRect(cx, cy, rotation);
        ret &= (prect.left <= x);
        ret &= (prect.right >= x);
        ret &= (prect.top <= y);
        ret &= (prect.bottom >= y);
        return ret;
    }

    /**
     * 電話を描画する四角形
     */
    private RectF getPhoneRect(float pcx, float pcy, int rotation) {
        RectF rect = new RectF();
        float zoom = (float) getBitmapZoom();
        switch (rotation) {
            case Configuration.ORIENTATION_PORTRAIT:
                rect.top = pcy - SetUpSideView.PHONE_HALF_LENGTH * zoom;
                rect.bottom = pcy + SetUpSideView.PHONE_HALF_LENGTH * zoom;
                rect.left = pcx - SetUpSideView.PHONE_HALF_WIDTH * zoom;
                rect.right = pcx + SetUpSideView.PHONE_HALF_WIDTH * zoom;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                rect.top = pcy - SetUpSideView.PHONE_HALF_WIDTH * zoom;
                rect.bottom = pcy + SetUpSideView.PHONE_HALF_WIDTH * zoom;
                rect.left = pcx - SetUpSideView.PHONE_HALF_LENGTH * zoom;
                rect.right = pcx + SetUpSideView.PHONE_HALF_LENGTH * zoom;
                break;
        }
        return rect;
    }

    /**
     * 電話を描画する
     */
    private void drawPhone(Canvas canvas, Paint paint, RectF rectf, int rotation) {
        canvas.drawRect(rectf, paint);
//        RectF rectt = new RectF();
//        rectt.left = rectf.left;
//        rectt.right = rectf.right;
//        rectt.top = rectf.top;
//        rectt.bottom = rectf.bottom;
//
//        float offset;
//        switch (rotation) {
//            case Configuration.ORIENTATION_LANDSCAPE:
//                offset = (rectf.right - rectf.left) / 5.0F;
//                rectt.right = rectt.left + offset;
//                canvas.drawRect(rectt, paint);
//                break;
//            case Configuration.ORIENTATION_PORTRAIT:
//                offset = (rectf.top - rectf.bottom) / 5.0F;
//                rectt.bottom = rectt.top + offset;
//                canvas.drawRect(rectt, paint);
//                break;
//        }
    }

    /**
     * 電話を描画する
     */
    private void drawPhone(Canvas canvas) {
        float cx, cy;
        RectF prectf;
        float zoom = (float) getBitmapZoom();
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー（右）（電話）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（中央）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（左）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // チューブトップ（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー（右）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（中央）（電話）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（左）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // チューブトップ（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー（右）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（中央）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（左）（電話）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_PHONE, prectf, getSetUpInfo().getOrientation());
                // チューブトップ（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // ハンドルバー（右）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（中央）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // ハンドルバー（左）（電話じゃない）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_NOT_PHONE, prectf, getSetUpInfo().getOrientation());
                // チューブトップ（電話）
                cx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                cy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                prectf = getPhoneRect(cx, cy, getSetUpInfo().getOrientation());
                drawPhone(canvas, SetUpSideView.PAINT_PHONE, prectf, getSetUpInfo().getOrientation());
                break;
        }
    }

    /**
     * 矢印を描画する
     */
    private void drawArrows(Canvas canvas) {
        float pcx, pcy;
        float zoom = (float) getBitmapZoom();
        int rotation = getSetUpInfo().getOrientation();
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                drawArrows(canvas, pcx, pcy, rotation);
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                drawArrows(canvas, pcx, pcy, rotation);
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                drawArrows(canvas, pcx, pcy, rotation);
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                drawArrows(canvas, pcx, pcy, rotation);
                break;
        }
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param rectf 電話機のイメージ領域
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowTopButtonRect(RectF rectf) {
        return new RectF(
                (rectf.left + rectf.right) / 2.0F - arrow_top.getWidth() / 2.0F,
                rectf.top - arrow_top.getHeight(),
                (rectf.left + rectf.right) / 2.0F + arrow_top.getWidth() / 2.0F,
                rectf.top);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param rectf 電話機のイメージ領域
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowBottomButtonRect(RectF rectf) {
        return new RectF(
                (rectf.left + rectf.right) / 2.0F - arrow_bottom.getWidth() / 2.0F,
                rectf.bottom,
                (rectf.left + rectf.right) / 2.0F + arrow_bottom.getWidth() / 2.0F,
                rectf.bottom + arrow_bottom.getHeight());
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param rectf 電話機のイメージ領域
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowLeftButtonRect(RectF rectf) {
        return new RectF(
                rectf.left - arrow_left.getWidth(),
                (rectf.top + rectf.bottom) / 2.0F - arrow_left.getHeight() / 2.0F,
                rectf.left,
                (rectf.top + rectf.bottom) / 2.0F + arrow_left.getHeight() / 2.0F);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param rectf 電話機のイメージ領域
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowRightButtonRect(RectF rectf) {
        return new RectF(
                rectf.right,
                (rectf.top + rectf.bottom) / 2.0F - arrow_right.getHeight() / 2.0F,
                rectf.right + arrow_right.getWidth(),
                (rectf.top + rectf.bottom) / 2.0F + arrow_right.getHeight() / 2.0F);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param pcx      電話機の位置の中央(X)
     * @param pcy      電話機の位置の中央(Y)
     * @param rotation 電話機の角度
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowTopButtonRect(float pcx, float pcy, int rotation) {
        RectF rectf = getPhoneRect(pcx, pcy, rotation);
        return getArrowTopButtonRect(rectf);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param pcx      電話機の位置の中央(X)
     * @param pcy      電話機の位置の中央(Y)
     * @param rotation 電話機の角度
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowBottomButtonRect(float pcx, float pcy, int rotation) {
        RectF rectf = getPhoneRect(pcx, pcy, rotation);
        return getArrowBottomButtonRect(rectf);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param pcx      電話機の位置の中央(X)
     * @param pcy      電話機の位置の中央(Y)
     * @param rotation 電話機の角度
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowLeftButtonRect(float pcx, float pcy, int rotation) {
        RectF rectf = getPhoneRect(pcx, pcy, rotation);
        return getArrowLeftButtonRect(rectf);
    }

    /**
     * 矢印ボタンイメージの領域を取得する
     *
     * @param pcx      電話機の位置の中央(X)
     * @param pcy      電話機の位置の中央(Y)
     * @param rotation 電話機の角度
     * @return 矢印ボタンイメージの領域
     */
    private RectF getArrowRightButtonRect(float pcx, float pcy, int rotation) {
        RectF rectf = getPhoneRect(pcx, pcy, rotation);
        return getArrowRightButtonRect(rectf);
    }

    /**
     * 矢印を描画する
     */
    private void drawArrows(Canvas canvas, float pcx, float pcy, int rotation) {
        RectF rectf = getPhoneRect(pcx, pcy, rotation);
        // 上
        canvas.drawBitmap(
                arrow_top,
                new Rect(0, 0, arrow_top.getWidth(), arrow_top.getHeight()),
                getArrowTopButtonRect(rectf),
                SetUpSideView.PAINT_BITMAP);
        // 下
        canvas.drawBitmap(arrow_bottom,
                new Rect(0, 0, arrow_bottom.getWidth(), arrow_bottom.getHeight()),
                getArrowBottomButtonRect(rectf),
                SetUpSideView.PAINT_BITMAP);
        // 右
        canvas.drawBitmap(arrow_right,
                new Rect(0, 0, arrow_right.getWidth(), arrow_right.getHeight()),
                getArrowRightButtonRect(rectf),
                SetUpSideView.PAINT_BITMAP);
        // 左
        canvas.drawBitmap(arrow_left,
                new Rect(0, 0, arrow_left.getWidth(), arrow_left.getHeight()),
                getArrowLeftButtonRect(rectf),
                SetUpSideView.PAINT_BITMAP);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        int rotation = getSetUpInfo().getOrientation();

        if (isInArrowTopButton(x, y, rotation)) {
            getSetUpInfo().rotateOrientation();
            invalidate();
        } else if (isInArrowBottomButton(x, y, rotation)) {
            getSetUpInfo().rotateOrientation();
            invalidate();
        } else if (isInArrowLeftButton(x, y, rotation)) {
            getSetUpInfo().rotateOrientation();
            invalidate();
        } else if (isInArrowRightButton(x, y, rotation)) {
            getSetUpInfo().rotateOrientation();
            invalidate();
        } else if (isInPhoneBarTopRight(x, y, rotation)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_BAR_RIGHT);
            invalidate();
        } else if (isInPhoneBarTopCenter(x, y, rotation)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_BAR_CENTER);
            invalidate();
        } else if (isInPhoneBarTopLeft(x, y, rotation)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_BAR_LEFT);
            invalidate();
        } else if (isInPhoneTubeTop(x, y, rotation)) {
            getSetUpInfo().setSetUpLocation(SetUpInfo.SETUP_TUBETOP);
            invalidate();
        }
        invalidate();

        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowTopButton(float x, float y, int rotation) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }

        ret = isInArrowTopButton(pcx, pcy, x, y, rotation);
        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowBottomButton(float x, float y, int rotation) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowBottomButton(pcx, pcy, x, y, rotation);
        return ret;
    }

    /**
     * 右の矢印ボタン内か？
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowRightButton(float x, float y, int rotation) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowRightButton(pcx, pcy, x, y, rotation);
        return ret;
    }

    /**
     * 左の矢印ボタン内か？
     *
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowLeftButton(float x, float y, int rotation) {
        boolean ret = true;
        float zoom = (float) getBitmapZoom();
        float pcx = 0, pcy = 0;
        switch (getSetUpInfo().getSetUpLocation()) {
            case SetUpInfo.SETUP_BAR_RIGHT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_RIGHT * zoom;
                break;
            case SetUpInfo.SETUP_BAR_CENTER:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_CENTER * zoom;
                break;
            case SetUpInfo.SETUP_BAR_LEFT:
                // ハンドルバー上に電話が存在する
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_BAR * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_BAR_LEFT * zoom;
                break;
            case SetUpInfo.SETUP_TUBETOP:
                // チューブトップ上に電話機がある
                // 電話
                pcx = bitmap.getWidth() * PHONE_LOC_X_ON_TUBE * zoom;
                pcy = bitmap.getHeight() * PHONE_LOC_Y_ON_TUBE * zoom;
                break;
        }
        ret = isInArrowLeftButton(pcx, pcy, x, y, rotation);
        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param pcx      中心点のpcx
     * @param pcy      中心点のpcy
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowTopButton(float pcx, float pcy, float x, float y, int rotation) {
        boolean ret = true;
        RectF rectf = getArrowTopButtonRect(pcx, pcy, getSetUpInfo().getOrientation());

        ret &= x >= rectf.left;
        ret &= x <= rectf.right;
        ret &= y >= rectf.top;
        ret &= y <= rectf.bottom;

        return ret;
    }

    /**
     * 上の矢印ボタン内か？
     *
     * @param pcx      中心点のpcx
     * @param pcy      中心点のpcy
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowBottomButton(float pcx, float pcy, float x, float y, int rotation) {
        boolean ret = true;
        RectF rectf = getArrowBottomButtonRect(pcx, pcy, getSetUpInfo().getOrientation());

        ret &= x >= rectf.left;
        ret &= x <= rectf.right;
        ret &= y >= rectf.top;
        ret &= y <= rectf.bottom;

        return ret;
    }

    /**
     * 右の矢印ボタン内か？
     *
     * @param pcx      中心点のpcx
     * @param pcy      中心点のpcy
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowRightButton(float pcx, float pcy, float x, float y, int rotation) {
        boolean ret = true;
        RectF rectf = getArrowRightButtonRect(pcx, pcy, getSetUpInfo().getOrientation());

        ret &= x >= rectf.left;
        ret &= x <= rectf.right;
        ret &= y >= rectf.top;
        ret &= y <= rectf.bottom;

        return ret;
    }

    /**
     * 左の矢印ボタン内か？
     *
     * @param pcx      中心点のpcx
     * @param pcy      中心点のpcy
     * @param x        X座標
     * @param y        Y座標
     * @param rotation 電話機の角度
     * @return 入っている？
     */
    private boolean isInArrowLeftButton(float pcx, float pcy, float x, float y, int rotation) {
        boolean ret = true;
        RectF rectf = getArrowLeftButtonRect(pcx, pcy, getSetUpInfo().getOrientation());

        ret &= x >= rectf.left;
        ret &= x <= rectf.right;
        ret &= y >= rectf.top;
        ret &= y <= rectf.bottom;

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
