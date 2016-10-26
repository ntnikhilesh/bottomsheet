package com.lb.three_phases_bottom_sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.ViewTransformer;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;




import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;





public class MyFragment2 extends BottomSheetFragment {
    private BottomSheetLayout mBottomSheetLayout;
    private int mMovingImageviewSize;
    private ImageView mMovingIconImageView;
    private Toolbar mLeftToolbar;
    private TextView mTitleExpanded, mTitleCollapsed;
    private ViewPropertyAnimator mTitleExpandedAnimation, mToolbarAnimation;
    private View mBottomsheetContentView;
    private int mMovingImageExpandedBottomSheetMarginLeft;
    private int mBottomSheetHeightExpanded, mBottomSheetHeightPeeked;
    private BottomSheetState mBottomSheetState = BottomSheetState.HIDDEN;
    private NestedScrollView mNestedScrollView;
    private View mBottomSheetHeader;
    private View mBottomSheetTopHeader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my2, container, false);
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        mBottomSheetHeader = view.findViewById(R.id.bottomsheetHeader);
        mBottomsheetContentView = view.findViewById(R.id.bottomsheetContentView);
        mMovingIconImageView = (ImageView) view.findViewById(R.id.movingIconImageView);
        mBottomSheetTopHeader = view.findViewById(R.id.bottomSheetTopHeader);
        mLeftToolbar = (Toolbar) view.findViewById(R.id.toolbar);



        //Share botton
        ImageView share_image=(ImageView)view.findViewById(R.id.ivshae_on_mpage);
        share_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey Check this cool site which allows you to never write addresses ever https://mapta.gs/Myoffice";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Maptag using"));
                //Toast.makeText(getContext(),"share",Toast.LENGTH_LONG).show();

            }
        });

        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        mBottomSheetHeightExpanded = getResources().getDimensionPixelSize(R.dimen.header_height_expanded);
        mBottomSheetHeightPeeked = getResources().getDimensionPixelSize(R.dimen.header_height_peeked);

        mMovingImageviewSize = getResources().getDimensionPixelSize(R.dimen.moving_image_collapsed_bottom_sheet_size);
        mTitleExpanded = (TextView) view.findViewById(R.id.expandedTitleTextView);
        mTitleCollapsed = (TextView) view.findViewById(R.id.collapsedTitleTextView);
        ((MarginLayoutParams) mBottomSheetTopHeader.getLayoutParams()).height = mBottomSheetHeightPeeked - mMovingImageviewSize / 2;
        ((MarginLayoutParams) mBottomsheetContentView.getLayoutParams()).topMargin = mBottomSheetHeightPeeked;


        mBottomSheetHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mBottomSheetLayout.expandSheet();
            }
        });

        mLeftToolbar.setNavigationIcon(R.drawable.call);
        mLeftToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetLayout.dismissSheet();
            }
        });
        mLeftToolbar.setAlpha(0);
        mBottomSheetTopHeader.setAlpha(0.0f);
        mBottomSheetTopHeader.setY(mMovingImageviewSize / 2);
        mBottomSheetHeader.setPadding(0, mMovingImageviewSize / 2, 0, 0);

        final int actionBarHeight = Utils.getActionBarHeight(getActivity());

        mMovingIconImageView.setPivotX(0);
        mTitleExpanded.setPivotX(0);
        mTitleExpanded.setPivotY(0);
        mTitleExpanded.setAlpha(0);
        mTitleCollapsed.setAlpha(0);
        mMovingImageExpandedBottomSheetMarginLeft = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_margin_left);

        mNestedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            final float actionBarIconPaddingText = getResources().getDimensionPixelSize(R.dimen.moving_text_collapsed_toolbar_top_and_bottom_padding);
            final float startMarginLeftText = getResources().getDimensionPixelSize(R.dimen.moving_text_expanded_toolbar_margin_left);
            boolean init = false;
            float startY, scaleDiff, startScale, targetY;
            //
            float scaleDiffTitle;
            float startYTitle;

            //Animating image section
            @Override
            public void onScrollChange(final NestedScrollView v,final int scrollX,final int scrollY,final int oldScrollX,final int oldScrollY)
            {

                float progress = Math.min(1, (float) scrollY / (mBottomSheetHeightExpanded - actionBarHeight));
                ((MarginLayoutParams) mBottomSheetTopHeader.getLayoutParams()).height = (int) Math.ceil(mBottomSheetHeightExpanded * (1 - progress) + progress * actionBarHeight);
                mBottomSheetTopHeader.requestLayout();
                if (!init) {
                    //imageview
                    startScale = mMovingIconImageView.getScaleX();
                    //
                    final int startMarginBottom = getResources().getDimensionPixelSize(R.dimen.header_view_start_margin_bottom);
                    final float targetSizeWhenBottomSheetExpanded = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_size);
                    final float startSizeWhenBottomSheetCollapsed = getResources().getDimensionPixelSize(R.dimen.moving_image_collapsed_bottom_sheet_size);
                    scaleDiff = (startSizeWhenBottomSheetCollapsed - targetSizeWhenBottomSheetExpanded) / startSizeWhenBottomSheetCollapsed;
                    final float scaleForImageView = 1 - scaleDiff;
                    startY = mBottomSheetHeightExpanded - startMarginBottom - mMovingIconImageView.getHeight() * scaleForImageView;
                    //
                    final float actionBarIconPadding = getResources().getDimensionPixelSize(R.dimen.moving_image_collapsed_toolbar_top_and_bottom_padding);
                    final float targetImageViewSize = actionBarHeight - actionBarIconPadding * 2;
                    targetY = actionBarIconPadding;
                    final float targetScale = targetImageViewSize / mMovingImageviewSize;
                    scaleDiff = startScale - targetScale;
                    //text:
                    scaleDiffTitle = 1 - (actionBarHeight - actionBarIconPaddingText * 2) / mTitleExpanded.getHeight();
                    startYTitle = mTitleExpanded.getY();
                    init = true;
                }
                final float newScale = startScale - progress * scaleDiff;
                mMovingIconImageView.setScaleX(newScale);
                mMovingIconImageView.setScaleY(newScale);
                mMovingIconImageView.setY(getValFromProgress(startY, targetY, progress));//  startY - progress * (startY - targetY));

                if (mToolbarAnimation != null) {
                    mToolbarAnimation.cancel();
                    mToolbarAnimation = null;
                    mLeftToolbar.setAlpha(1);
                    mLeftToolbar.setY(0);
                }
                if (mTitleExpandedAnimation != null) {
                    mTitleExpandedAnimation.cancel();
                    mTitleExpandedAnimation = null;
                }

                mTitleExpanded.setScaleX(1f - progress * scaleDiffTitle);
                mTitleExpanded.setScaleY(1f - progress * scaleDiffTitle);
                mTitleExpanded.setX(startMarginLeftText + (progress * (2 * actionBarHeight - startMarginLeftText)));
                mTitleExpanded.setY(mMovingIconImageView.getY() + mMovingImageviewSize * mMovingIconImageView.getScaleY() / 2 - mTitleExpanded.getHeight() * mTitleExpanded.getScaleY() / 2);

                mTitleExpanded.setAlpha(progress <= 0.7f ? 1f : progress >= 0.9f ? 0f : 1 - ((progress - 0.7f) / 0.2f));
                mTitleCollapsed.setAlpha(progress <= 0.9f ? 0f : progress >= 1f ? 1f : (progress - 0.9f) / 0.1f);

            }
        });

        return view;
    }



    @Override
    public ViewTransformer getViewTransformer()
    {
        final float targetSizeWhenBottomSheetExpanded = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_size);
        final float startSizeWhenBottomSheetCollapsed = getResources().getDimensionPixelSize(R.dimen.moving_image_collapsed_bottom_sheet_size);
        final float startMarginBottom = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_margin_bottom);
        return new BaseViewTransformer() {
            boolean init = false;
            float mOriginalContactPhotoXCoordinate;
            float mOriginalBottomSheetBackgroundImageViewY;
            float scaleDiff;

            @Override
            public void transformView(final float translation, final float maxTranslation, final float peekedTranslation, final BottomSheetLayout parent, final View view) {
                if (!init) {
                    mOriginalBottomSheetBackgroundImageViewY = mMovingImageviewSize / 2;
                    mOriginalContactPhotoXCoordinate = mMovingIconImageView.getX();
                    scaleDiff = (startSizeWhenBottomSheetCollapsed - targetSizeWhenBottomSheetExpanded) / startSizeWhenBottomSheetCollapsed;
                    init = true;
                }
                if (translation < mBottomSheetHeightPeeked) {
                    //hidden<->peeked or hidden
                    reportState(translation == 0 ? BottomSheetState.HIDDEN : BottomSheetState.HIDDEN_PEEKED);
                    return;
                }
                if (translation == mBottomSheetHeightPeeked) {
                    //peek
                    reportState(BottomSheetState.PEEKED);
                } else {
                    //peek->expand
                    reportState(translation == maxTranslation ? BottomSheetState.EXPANDED : BottomSheetState.PEEKED_EXPANDED);
                }
                float progress = (translation - mBottomSheetHeightPeeked) / (maxTranslation - mBottomSheetHeightPeeked);
                mMovingIconImageView.setX(mOriginalContactPhotoXCoordinate - progress * (mOriginalContactPhotoXCoordinate - mMovingImageExpandedBottomSheetMarginLeft));
                final float scaleForImageView = 1 - progress * scaleDiff;
               mMovingIconImageView.setScaleX(scaleForImageView);
                mMovingIconImageView.setScaleY(scaleForImageView);
                final float newMovingIconImageViewY = progress * (mBottomSheetHeightExpanded - startMarginBottom - mMovingIconImageView.getHeight() * scaleForImageView);
                mMovingIconImageView.setY(newMovingIconImageViewY);
                final float newBottomSheetBackgroundImageContainerY = mOriginalBottomSheetBackgroundImageViewY * (1 - progress);
                mBottomSheetTopHeader.setY(newBottomSheetBackgroundImageContainerY);
                mBottomSheetTopHeader.getLayoutParams().height =
                        (int) getValFromProgress(mBottomSheetHeightPeeked - mMovingImageviewSize / 2, mBottomSheetHeightExpanded, progress);
                ((MarginLayoutParams) mBottomsheetContentView.getLayoutParams()).topMargin = (int) getValFromProgress(mBottomSheetHeightPeeked, mBottomSheetHeightExpanded, progress);
                mBottomsheetContentView.requestLayout();
                mBottomSheetTopHeader.requestLayout();
            }
        };
    }

    private static float getValFromProgress(float start, float end, float progress) {
        return start + progress * (end - start);
    }


    protected void reportState(final BottomSheetState state) {
        if (mBottomSheetState == state)
            return;
        mBottomSheetState = state;
        if (state != BottomSheetState.EXPANDED) {
            if (mToolbarAnimation != null) {
                mToolbarAnimation.cancel();
                mToolbarAnimation = null;
            }
            if (mTitleExpandedAnimation != null) {
                mTitleExpandedAnimation.cancel();
                mTitleExpandedAnimation = null;
            }
            mTitleCollapsed.setAlpha(0);
            mTitleExpanded.setAlpha(0);
            mLeftToolbar.setAlpha(0);
            mTitleExpanded.setVisibility(View.INVISIBLE);
            mLeftToolbar.setVisibility(View.INVISIBLE);
        }
        switch (state) {
            case HIDDEN:
                break;
            case HIDDEN_PEEKED:
                break;
            case PEEKED:
                mBottomSheetTopHeader.animate().cancel();
                final ViewPropertyAnimator animator = mBottomSheetTopHeader.animate();
                animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                animator.alpha(0);
                mBottomSheetHeader.setVisibility(View.VISIBLE);
                break;
            case PEEKED_EXPANDED:
                mBottomSheetTopHeader.animate().cancel();
                mBottomSheetTopHeader.animate().alpha(1);
                mBottomSheetHeader.setVisibility(View.GONE);
                break;
            case EXPANDED:
                final float startMarginLeftText = getResources().getDimensionPixelSize(R.dimen.moving_text_expanded_toolbar_margin_left);
                mTitleExpanded.setX(startMarginLeftText);
                mTitleExpanded.setY(mMovingIconImageView.getY() + mMovingImageviewSize * mMovingIconImageView.getScaleY() / 2 - mTitleExpanded.getHeight() / 2);
                mBottomSheetHeader.setVisibility(View.GONE);
                if (mToolbarAnimation == null) {
                    mTitleExpanded.setVisibility(View.VISIBLE);
                    mLeftToolbar.setVisibility(View.VISIBLE);
                    mLeftToolbar.setY(-mLeftToolbar.getHeight() / 3);
                    mToolbarAnimation = mLeftToolbar.animate().setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                    mToolbarAnimation.alpha(1).y(0).start();
                    mTitleExpanded.setAlpha(0);
                    mTitleExpandedAnimation = mTitleExpanded.animate().setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                    mTitleExpandedAnimation.alpha(1).start();
                }
                break;
        }
    }

    public void setBottomSheetLayout(final BottomSheetLayout bottomSheetLayout) {
        mBottomSheetLayout = bottomSheetLayout;
    }



    //Exessing Google contacts

    /*

    public static void printAllContacts(ContactsService myService)
            throws ServiceException, IOException {
        // Request the feed
        URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
        ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
        // Print the results
        System.out.println(resultFeed.getTitle().getPlainText());
        for (ContactEntry entry : resultFeed.getEntries()) {
            if (entry.hasName()) {
                Attributes.Name name = entry.getName();
                if (name.hasFullName()) {
                    String fullNameToDisplay = name.getFullName().getValue();
                    if (name.getFullName().hasYomi()) {
                        fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + fullNameToDisplay);
                } else {
                    System.out.println("\t\t (no full name found)");
                }
                if (name.hasNamePrefix()) {
                    System.out.println("\t\t" + name.getNamePrefix().getValue());
                } else {
                    System.out.println("\t\t (no name prefix found)");
                }
                if (name.hasGivenName()) {
                    String givenNameToDisplay = name.getGivenName().getValue();
                    if (name.getGivenName().hasYomi()) {
                        givenNameToDisplay += " (" + name.getGivenName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + givenNameToDisplay);
                } else {
                    System.out.println("\t\t (no given name found)");
                }
                if (name.hasAdditionalName()) {
                    String additionalNameToDisplay = name.getAdditionalName().getValue();
                    if (name.getAdditionalName().hasYomi()) {
                        additionalNameToDisplay += " (" + name.getAdditionalName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + additionalNameToDisplay);
                } else {
                    System.out.println("\t\t (no additional name found)");
                }
                if (name.hasFamilyName()) {
                    String familyNameToDisplay = name.getFamilyName().getValue();
                    if (name.getFamilyName().hasYomi()) {
                        familyNameToDisplay += " (" + name.getFamilyName().getYomi() + ")";
                    }
                    System.out.println("\t\t" + familyNameToDisplay);
                } else {
                    System.out.println("\t\t (no family name found)");
                }
                if (name.hasNameSuffix()) {
                    System.out.println("\t\t" + name.getNameSuffix().getValue());
                } else {
                    System.out.println("\t\t (no name suffix found)");
                }
            } else {
                System.out.println("\t (no name found)");
            }
            System.out.println("Email addresses:");
            for (Email email : entry.getEmailAddresses()) {
                System.out.print(" " + email.getAddress());
                if (email.getRel() != null) {
                    System.out.print(" rel:" + email.getRel());
                }
                if (email.getLabel() != null) {
                    System.out.print(" label:" + email.getLabel());
                }
                if (email.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }
            System.out.println("IM addresses:");
            for (Im im : entry.getImAddresses()) {
                System.out.print(" " + im.getAddress());
                if (im.getLabel() != null) {
                    System.out.print(" label:" + im.getLabel());
                }
                if (im.getRel() != null) {
                    System.out.print(" rel:" + im.getRel());
                }
                if (im.getProtocol() != null) {
                    System.out.print(" protocol:" + im.getProtocol());
                }
                if (im.getPrimary()) {
                    System.out.print(" (primary) ");
                }
                System.out.print("\n");
            }
            System.out.println("Groups:");
            for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                String groupHref = group.getHref();
                System.out.println("  Id: " + groupHref);
            }
            System.out.println("Extended Properties:");
            for (ExtendedProperty property : entry.getExtendedProperties()) {
                if (property.getValue() != null) {
                    System.out.println("  " + property.getName() + "(value) = " +
                            property.getValue());
                } else if (property.getXmlBlob() != null) {
                    System.out.println("  " + property.getName() + "(xmlBlob)= " +
                            property.getXmlBlob().getBlob());
                }
            }
            Link photoLink = entry.getContactPhotoLink();
            String photoLinkHref = photoLink.getHref();
            System.out.println("Photo Link: " + photoLinkHref);
            if (photoLink.getEtag() != null) {
                System.out.println("Contact Photo's ETag: " + photoLink.getEtag());
            }
            System.out.println("Contact's ETag: " + entry.getEtag());
        }
    }

    */

}
