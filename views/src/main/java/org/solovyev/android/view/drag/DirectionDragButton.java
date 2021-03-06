/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.view.drag;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.solovyev.android.view.R;
import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.NumberParser;
import org.solovyev.common.text.StringCollections;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 7/17/11
 * Time: 10:25 PM
 */
public class DirectionDragButton extends DragButton {

	@Nonnull
	private final static Float DEFAULT_DIRECTION_TEXT_SCALE_FLOAT = 0.33f;

	@Nonnull
	private final static Integer DEFAULT_DIRECTION_TEXT_ALPHA = 140;

	private final static int DEFAULT_DIRECTION_TEXT_COLOR = Color.WHITE;

	@Nonnull
	private final static String DEFAULT_DIRECTION_TEXT_SCALE = "0.33;0.33;0.33;0.33";

	protected static class DirectionTextData {

		@Nonnull
		private final GuiDragDirection guiDragDirection;

		@Nonnull
		private String text;

		@Nonnull
		private Point2d position;

		@Nonnull
		private TextPaint paint;

		@Nonnull
		private Float textScale = 0.5f;

		private boolean showText = true;

		private DirectionTextData(@Nonnull GuiDragDirection guiDragDirection, @Nonnull String text) {
			this.guiDragDirection = guiDragDirection;
			this.text = text;
		}

		@Nonnull
		public GuiDragDirection getGuiDragDirection() {
			return guiDragDirection;
		}

		@Nonnull
		public String getText() {
			return text;
		}

		public void setText(@Nonnull String text) {
			this.text = text;
		}

		@Nonnull
		public Point2d getPosition() {
			return position;
		}

		public void setPosition(@Nonnull Point2d position) {
			this.position = position;
		}

		@Nonnull
		public TextPaint getPaint() {
			return paint;
		}

		public void setPaint(@Nonnull TextPaint paint) {
			this.paint = paint;
		}

		@Nonnull
		public Float getTextScale() {
			return textScale;
		}

		public void setTextScale(@Nonnull Float textScale) {
			this.textScale = textScale;
		}

		public boolean isShowText() {
			return showText;
		}

		public void setShowText(boolean showText) {
			this.showText = showText;
		}
	}

	protected static enum GuiDragDirection {
		up(DragDirection.up, 0) {
			@Override
			public int getAttributeId() {
				return R.styleable.DirectionDragButton_textUp;
			}

			@Nonnull
			@Override
			public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
				return getUpDownTextPosition(paint, basePaint, text, baseText, 1, w, h);
			}
		},
		down(DragDirection.down, 2) {
			@Override
			public int getAttributeId() {
				return R.styleable.DirectionDragButton_textDown;
			}

			@Nonnull
			@Override
			public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
				return getUpDownTextPosition(paint, basePaint, text, baseText, -1, w, h);
			}
		},
		left(DragDirection.left, 3) {
			@Override
			public int getAttributeId() {
				return R.styleable.DirectionDragButton_textLeft;
			}

			@Nonnull
			@Override
			public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
				return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, true);
			}
		},

		right(DragDirection.right, 1) {
			@Override
			public int getAttributeId() {
				return R.styleable.DirectionDragButton_textRight;
			}

			@Nonnull
			@Override
			public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
				return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, false);
			}
		};

		@Nonnull
		private final DragDirection dragDirection;

		private final int attributePosition;

		GuiDragDirection(@Nonnull DragDirection dragDirection, int attributePosition) {
			this.dragDirection = dragDirection;
			this.attributePosition = attributePosition;
		}

		public abstract int getAttributeId();

		public int getAttributePosition() {
			return attributePosition;
		}

		@Nonnull
		public abstract Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h);

		@Nonnull
		private static Point2d getLeftRightTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, CharSequence text, @Nonnull CharSequence baseText, int w, int h, boolean left) {
			final Point2d result = new Point2d();

			if (left) {
				float width = paint.measureText(" ");
				result.setX(width);
			} else {
				float width = paint.measureText(text.toString() + " ");
				result.setX(w - width);
			}

			float selfHeight = paint.ascent() + paint.descent();

			basePaint.measureText(Strings.getNotEmpty(baseText, "|"));

			result.setY(h / 2 - selfHeight / 2);

			return result;
		}

		@Nonnull
		private static Point2d getUpDownTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, float direction, int w, int h) {
			final Point2d result = new Point2d();

			float width = paint.measureText(text.toString() + " ");
			result.setX(w - width);

			float selfHeight = paint.ascent() + paint.descent();

			basePaint.measureText(Strings.getNotEmpty(baseText, "|"));

			if (direction < 0) {
				result.setY(h / 2 + h / 3 - selfHeight / 2);
			} else {
				result.setY(h / 2 - h / 3 - selfHeight / 2);
			}

			return result;
		}

		@Nullable
		public static GuiDragDirection valueOf(@Nonnull DragDirection dragDirection) {
			for (GuiDragDirection guiDragDirection : values()) {
				if (guiDragDirection.dragDirection == dragDirection) {
					return guiDragDirection;
				}
			}
			return null;
		}
	}

	@Nonnull
	private final Map<GuiDragDirection, DirectionTextData> directionTextDataMap = new EnumMap<GuiDragDirection, DirectionTextData>(GuiDragDirection.class);

	@Nonnull
	private String directionTextScale = DEFAULT_DIRECTION_TEXT_SCALE;

	@Nonnull
	private Integer directionTextAlpha = DEFAULT_DIRECTION_TEXT_ALPHA;

	private int directionTextColor = DEFAULT_DIRECTION_TEXT_COLOR;

	private boolean initialized = false;

	public DirectionDragButton(Context context, @Nonnull AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public DirectionDragButton(Context context, @Nonnull DirectionDragButtonDef directionDragButtonDef) {
		super(context, directionDragButtonDef);
		init(context, directionDragButtonDef);
	}

	private void init(@Nonnull Context context,
					  @Nonnull DirectionDragButtonDef directionDragButtonDef) {
		for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
			final CharSequence directionText = directionDragButtonDef.getText(guiDragDirection.dragDirection);
			this.directionTextDataMap.put(guiDragDirection, new DirectionTextData(guiDragDirection, Strings.getNotEmpty(directionText, "")));
		}

		this.initialized = true;
	}

	public void applyDef(@Nonnull DirectionDragButtonDef directionDragButtonDef) {
		super.applyDef(directionDragButtonDef);

		for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
			final CharSequence directionText = directionDragButtonDef.getText(guiDragDirection.dragDirection);
			this.directionTextDataMap.put(guiDragDirection, new DirectionTextData(guiDragDirection, Strings.getNotEmpty(directionText, "")));
		}
	}

	private void init(@Nonnull Context context, @Nonnull AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DirectionDragButton);

		for (int i = 0; i < a.getIndexCount(); i++) {
			int attr = a.getIndex(i);

			if (a.hasValue(attr)) {
				switch (attr) {
					case R.styleable.DirectionDragButton_directionTextColor:
						this.directionTextColor = a.getColor(attr, DEFAULT_DIRECTION_TEXT_COLOR);
						break;
					case R.styleable.DirectionDragButton_directionTextScale:
						this.directionTextScale = a.getString(attr);
						break;
					case R.styleable.DirectionDragButton_directionTextAlpha:
						this.directionTextAlpha = a.getInt(attr, DEFAULT_DIRECTION_TEXT_ALPHA);
						break;
					default:
						// try drag direction text
						for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
							if (guiDragDirection.getAttributeId() == attr) {
								this.directionTextDataMap.put(guiDragDirection, new DirectionTextData(guiDragDirection, a.getString(attr)));
								break;
							}
						}
						break;
				}
			}
		}

		for (Map.Entry<GuiDragDirection, Float> entry : getDirectionTextScales().entrySet()) {
			final DirectionTextData dtd = directionTextDataMap.get(entry.getKey());
			if (dtd != null) {
				dtd.setTextScale(entry.getValue());
			}
		}

		initialized = true;
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		measureText();
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		measureText();
	}

	protected void measureText() {

		if (initialized) {
			final Paint basePaint = getPaint();
			final Resources resources = getResources();

			for (DirectionTextData directionTextData : directionTextDataMap.values()) {
				initDirectionTextPaint(basePaint, directionTextData, resources);

				final GuiDragDirection guiDragDirection = directionTextData.getGuiDragDirection();
				final String directionText = directionTextData.getText();
				final Paint directionPaint = directionTextData.getPaint();

				directionTextData.setPosition(guiDragDirection.getTextPosition(directionPaint, basePaint, directionText, getText(), getWidth(), getHeight()));
			}
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final TextPaint paint = getPaint();
		final Resources resources = getResources();

		for (DirectionTextData directionTextData : directionTextDataMap.values()) {
			if (directionTextData.isShowText()) {
				initDirectionTextPaint(paint, directionTextData, resources);
				final String text = directionTextData.getText();
				final Point2d position = directionTextData.getPosition();
				canvas.drawText(text, 0, text.length(), position.getX(), position.getY(), directionTextData.getPaint());
			}
		}
	}

	protected void initDirectionTextPaint(@Nonnull Paint basePaint,
										  @Nonnull DirectionTextData directionTextData,
										  @Nonnull Resources resources) {
		final TextPaint directionTextPaint = new TextPaint(basePaint);

		directionTextPaint.setColor(directionTextColor);
		directionTextPaint.setAlpha(getDirectionTextAlpha());
		directionTextPaint.setTextSize(basePaint.getTextSize() * directionTextData.getTextScale());

		directionTextData.setPaint(directionTextPaint);
	}

	protected int getDirectionTextAlpha() {
		return directionTextAlpha;
	}

	@SuppressWarnings("UnusedDeclaration")
	@Nullable
	public String getTextUp() {
		return getText(GuiDragDirection.up);
	}

	@SuppressWarnings("UnusedDeclaration")
	@Nullable
	public String getTextDown() {
		return getText(GuiDragDirection.down);
	}

	@Nullable
	public String getText(@Nonnull DragDirection direction) {
		final GuiDragDirection guiDragDirection = GuiDragDirection.valueOf(direction);
		return guiDragDirection == null ? null : getText(guiDragDirection);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void showDirectionText(boolean showDirectionText, @Nonnull DragDirection direction) {
		final GuiDragDirection guiDragDirection = GuiDragDirection.valueOf(direction);
		final DirectionTextData directionTextData = this.directionTextDataMap.get(guiDragDirection);
		if (directionTextData != null) {
			directionTextData.setShowText(showDirectionText);
		}
	}

	@Nullable
	private String getText(@Nonnull GuiDragDirection direction) {
		DirectionTextData directionTextData = this.directionTextDataMap.get(direction);
		if (directionTextData == null) {
			return null;
		} else {
			if (directionTextData.isShowText()) {
				return directionTextData.getText();
			} else {
				return null;
			}
		}
	}


	@Nonnull
	public String getDirectionTextScale() {
		return directionTextScale;
	}

	@Nonnull
	private Map<GuiDragDirection, Float> getDirectionTextScales() {
		final List<Float> scales = StringCollections.split(getDirectionTextScale(), ";", NumberParser.of(Float.class));

		final Map<GuiDragDirection, Float> result = new HashMap<GuiDragDirection, Float>();
		for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
			result.put(guiDragDirection, DEFAULT_DIRECTION_TEXT_SCALE_FLOAT);
		}

		if (scales.size() == 1) {
			final Float scale = scales.get(0);
			for (Map.Entry<GuiDragDirection, Float> entry : result.entrySet()) {
				entry.setValue(scale);
			}
		} else {
			for (int i = 0; i < scales.size(); i++) {
				for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
					if (guiDragDirection.getAttributePosition() == i) {
						result.put(guiDragDirection, scales.get(i));
					}
				}
			}
		}

		return result;
	}

}
