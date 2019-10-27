package com.example.mlkittest.Model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeGroupAlgorithm implements IGroupAlgorithm {
    Comparator<UnitImageFile> unitImageComparator = (UnitImageFile o1, UnitImageFile o2) -> {
        LocalDateTime t1 = o1.getCreationTime();
        LocalDateTime t2 = o2.getCreationTime();

        return t1.compareTo(t2);
    };

    private static long epsilon = 600;

    @Override
    public List<UnitImageFileGroup> processImages(List<UnitImageFile> images) {
        // 정렬
        Collections.sort(images, unitImageComparator);

        // 자르기
        List<UnitImageFileGroup> groups = new ArrayList<>();
        for (int imageIdx = 0; imageIdx < images.size(); imageIdx++) {
            // 시작할 때 초기화
            if (imageIdx == 0) {
                groups.add(new UnitImageFileGroup());
                continue;
            }

            UnitImageFile curImage = images.get(imageIdx);
            UnitImageFile prevImage = images.get(imageIdx - 1);

            LocalDateTime curTime = curImage.getCreationTime();
            LocalDateTime prevTime = prevImage.getCreationTime();

            if (TimeGroupAlgorithm.difference(curTime, prevTime) > epsilon) {
                UnitImageFileGroup newGroup = new UnitImageFileGroup();
                newGroup.addImage(curImage);
                groups.add(newGroup);
            } else {
                UnitImageFileGroup lastGroup = groups.get(groups.size() - 1);
                lastGroup.addImage(curImage);
            }
        }

        return groups;
    }

    private static long difference(LocalDateTime t1, LocalDateTime t2) {
        long t1InSeconds = t1.toEpochSecond(ZoneOffset.UTC);
        long t2InSeconds = t2.toEpochSecond(ZoneOffset.UTC);

        return t1InSeconds - t2InSeconds;
    }
}