package com.example.user.contacts.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.contacts.R;
import com.example.user.contacts.listeners.Subscriber;

public class IndexLayoutManager implements Subscriber {

    private TextView stickyIndex;
    private RecyclerView indexList;

    public IndexLayoutManager(RelativeLayout rl) {
        this.stickyIndex = rl.findViewById(R.id.sticky_index);
    }

    private Boolean isHeader (TextView prev, TextView act) {
        if (isSameChar(prev.getText().charAt(0), act.getText().charAt(0))) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    private Boolean isSameChar (char prev, char curr) {
        if (Character.toLowerCase(prev) == Character.toLowerCase(curr)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private void updatePosBasedOnReferenceList (RecyclerView referenceRv) {
        View firstVisibleView = referenceRv.getChildAt(0);
        int actual = referenceRv.getChildPosition(firstVisibleView);
        int top = 0;
        try{
            top = firstVisibleView.getTop();
        }catch (Exception ex){
            Log.d("Scroll", "On the top");
        }
        ((LinearLayoutManager) indexList.getLayoutManager()).scrollToPositionWithOffset(actual, top);
    }

    @Override
    public void update(RecyclerView referenceList, float dx, float dy) {
        if (indexList != null) {
            updatePosBasedOnReferenceList(referenceList);

            View firstVisibleView = indexList.getChildAt(0);
            View secondVisibleView = indexList.getChildAt(1);

            if(firstVisibleView != null){
                TextView firstRowIndex = firstVisibleView.findViewById(R.id.sticky_row_index);
                TextView secondRowIndex = firstVisibleView.findViewById(R.id.sticky_row_index);
                if(secondVisibleView != null) secondRowIndex = secondVisibleView.findViewById(R.id.sticky_row_index);

                int visibleRange = indexList.getChildCount();
                int actual = indexList.getChildPosition(firstVisibleView);
                int next = actual + 1;
                int last = actual + visibleRange;

                // RESET STICKY LETTER INDEX
                stickyIndex.setText(String.valueOf(getIndexContext(firstRowIndex)).toUpperCase());
                stickyIndex.setVisibility(TextView.VISIBLE);
                firstRowIndex.setAlpha(1);

                if (dy > 0) {
                    // USER SCROLLING DOWN THE RecyclerView
                    if (next <= last) {
                        if (isHeader(firstRowIndex, secondRowIndex)) {
                            stickyIndex.setVisibility(TextView.INVISIBLE);
                            firstRowIndex.setVisibility(TextView.VISIBLE);
                            firstRowIndex.setAlpha(1 - (Math.abs(firstVisibleView.getY()) / firstRowIndex.getHeight()));
                            secondRowIndex.setVisibility(TextView.VISIBLE);
                        } else {
                            firstRowIndex.setVisibility(TextView.INVISIBLE);
                            stickyIndex.setVisibility(TextView.VISIBLE);
                        }
                    }
                } else {
                    // USER IS SCROLLING UP THE RecyclerVIew
                    if (next <= last) {
                        // RESET FIRST ROW STATE
                        firstRowIndex.setVisibility(TextView.INVISIBLE);
                        if ((isHeader(firstRowIndex, secondRowIndex) || (getIndexContext(firstRowIndex) != getIndexContext(secondRowIndex))) && isHeader(firstRowIndex, secondRowIndex)) {
                            stickyIndex.setVisibility(TextView.INVISIBLE);
                            firstRowIndex.setVisibility(TextView.VISIBLE);
                            firstRowIndex.setAlpha(1 - (Math.abs(firstVisibleView.getY()) / firstRowIndex.getHeight()));
                            secondRowIndex.setVisibility(TextView.VISIBLE);
                        } else {
                            secondRowIndex.setVisibility(TextView.INVISIBLE);
                        }
                    }
                }

                if (stickyIndex.getVisibility() == TextView.VISIBLE) {
                    firstRowIndex.setVisibility(TextView.INVISIBLE);
                }

            }else {
                stickyIndex.setVisibility(TextView.INVISIBLE);
            }
        }
    }

    public void setIndexList(RecyclerView indexList) {
        this.indexList = indexList;
    }
    private char getIndexContext (TextView index) {
        return index.getText().charAt(0);
    }

    public TextView getStickyIndex() {
        return stickyIndex;
    }
}
