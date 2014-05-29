package au.com.twobit.yosane.api.comparator;

import java.util.Comparator;

import au.com.twobit.yosane.api.Image;

public class ImageComparator implements Comparator<Image> {

    @Override
    public int compare(Image imgOne , Image imgTwo) {
        if ( imgOne == null && imgTwo == null ) {
            return 0;
        } else if ( imgOne == null ) {
            return -1;
        } else if ( imgTwo == null ) {
            return 1;
        }
        return Integer.compare( imgOne.getOrdering(), imgTwo.getOrdering());
    }

}
