package com.example.mlkittest.Model;

import java.util.ArrayList;
import java.util.List;

public class UnitImageGroup {
    protected List<UnitImage> images;

    public UnitImageGroup () {
        this.images = new ArrayList<UnitImage>();
    }

    public UnitImageGroup (List<UnitImage> images) {
        this.images = images;
    }

    public List<UnitImage> getImages() {
        return images;
    }

    public void addImage(UnitImage image) {
        images.add(image);
    }
}
