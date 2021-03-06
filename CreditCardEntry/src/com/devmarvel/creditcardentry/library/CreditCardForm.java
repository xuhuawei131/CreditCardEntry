package com.devmarvel.creditcardentry.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardEntry;

public class CreditCardForm extends RelativeLayout {

	private CreditCardEntry entry;
	private boolean includeZip = true;
	private boolean includeHelper;
	private int textHelperColor;
	private Drawable inputBackground;
	private String cardNumberHint = "1234 5678 9012 3456";

	public CreditCardForm(Context context) {
		this(context, null);
	}

	public CreditCardForm(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CreditCardForm(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if(!isInEditMode()) {

			// If the attributes are available, use them to color the icon
			if(attrs != null){

				TypedArray typedArray = null;
				try {
					typedArray = context.getTheme().obtainStyledAttributes(
                  attrs,
                  R.styleable.CreditCardForm,
                  0,
                  0
          );

					this.cardNumberHint = typedArray.getString(R.styleable.CreditCardForm_card_number_hint);
					this.includeZip = typedArray.getBoolean(R.styleable.CreditCardForm_include_zip, true);
					this.includeHelper = typedArray.getBoolean(R.styleable.CreditCardForm_include_helper, true);
					this.textHelperColor = typedArray.getColor(R.styleable.CreditCardForm_helper_text_color, getResources().getColor(R.color.text_helper_color));
					this.inputBackground = typedArray.getDrawable(R.styleable.CreditCardForm_input_background);
				} finally {
					if (typedArray != null) typedArray.recycle();
				}
			}

			// defaults if not set by user
			if(cardNumberHint == null) cardNumberHint = "1234 5678 9012 3456";
			if(inputBackground == null) {
				//noinspection deprecation
				inputBackground = context.getResources().getDrawable(R.drawable.background_white);
			}
		}

		init(context);
	}

	private void init(Context context) {
		// the wrapper layout
		LinearLayout layout = new LinearLayout(context);
		layout.setId(R.id.cc_layout);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(LinearLayout.HORIZONTAL);
		params.setMargins(0, 0, 0, 0);
		layout.setLayoutParams(params);
		layout.setPadding(0,0,0,0);
		//noinspection deprecation
		layout.setBackgroundDrawable(inputBackground);

		// set up the card image container and images
		FrameLayout cardImageFrame = new FrameLayout(context);
		LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		frameParams.gravity = Gravity.CENTER_VERTICAL;
		cardImageFrame.setLayoutParams(frameParams);
		cardImageFrame.setFocusable(true);
		cardImageFrame.setPadding(10, 0, 0, 0);

		ImageView cardFrontImage = new ImageView(context);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cardFrontImage.setLayoutParams(layoutParams);
		cardFrontImage.setImageResource(CardType.INVALID.frontResource);
		cardImageFrame.addView(cardFrontImage);

		ImageView cardBackImage = new ImageView(context);
		layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cardBackImage.setLayoutParams(layoutParams);
		cardBackImage.setImageResource(CardType.INVALID.backResource);
		cardBackImage.setVisibility(View.GONE);
		cardImageFrame.addView(cardBackImage);
		layout.addView(cardImageFrame);

		// add the data entry form
		LinearLayout.LayoutParams entryParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		entryParams.gravity = Gravity.CENTER_VERTICAL;
		entry = new CreditCardEntry(context, includeZip);

		// this obnoxious 6 for bottom padding is to make the damn text centered on the image... if you know a better way... PLEASE HELP
		entry.setPadding(0, 0, 0, 6);
		entry.setLayoutParams(entryParams);

		// set any passed in attrs
		entry.setCardImageView(cardFrontImage);
		entry.setBackCardImage(cardBackImage);
		entry.setCardNumberHint(cardNumberHint);

		this.addView(layout);

		// set up optional helper text view
		if (includeHelper) {
			TextView textHelp = new TextView(context);
			textHelp.setText(getResources().getString(R.string.CreditCardNumberHelp));
			textHelp.setTextColor(this.textHelperColor);
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.BELOW, layout.getId());
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.setMargins(0, 15, 0, 20);
			textHelp.setLayoutParams(layoutParams);
			entry.setTextHelper(textHelp);
			this.addView(textHelp);
		}

		layout.addView(entry);
	}

	public void setOnCardValidCallback(CardValidCallback callback) {
		entry.setOnCardValidCallback(callback);
	}

	/**
	 * all internal components will be attached this same focus listener
	 */
	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		entry.setOnFocusChangeListener(l);
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		return entry.getOnFocusChangeListener();
	}

	@SuppressWarnings("unused")
	public boolean isCreditCardValid() {
		return entry.isCreditCardValid();
	}
	
	@SuppressWarnings("unused")
	public CreditCard getCreditCard() {
		return entry.getCreditCard();
	}

	/**
	 * request focus for the credit card field
	 */
	@SuppressWarnings("unused")
	public void focusCreditCard() {
		entry.focusCreditCard();
	}

	/**
	 * request focus for the expiration field
	 */
	@SuppressWarnings("unused")
	public void focusExp() {
		entry.focusExp();
	}

	/**
	 * request focus for the security code field
	 */
	@SuppressWarnings("unused")
	public void focusSecurityCode() {
		entry.focusSecurityCode();
	}

	/**
	 * request focus for the zip field (IF it's enabled)
	 */
	@SuppressWarnings("unused")
	public void focusZip() {
		entry.focusZip();
	}
}
