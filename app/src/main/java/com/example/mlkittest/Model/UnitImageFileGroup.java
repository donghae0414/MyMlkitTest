package com.example.mlkittest.Model;

import java.util.ArrayList;
import java.util.List;

public class UnitImageFileGroup {
    protected List<UnitImageFile> images;

    public UnitImageFileGroup () {
        this.images = new ArrayList<UnitImageFile>();
    }

    public UnitImageFileGroup (List<UnitImageFile> images) {
        this.images = images;
    }

    public List<UnitImageFile> getImages() {
        return images;
    }

    public void addImage(UnitImageFile image) {
        images.add(image);
    }
}
