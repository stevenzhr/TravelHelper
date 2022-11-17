package hotelapp.hotelDataCollection;

public class FusionDataCollection {
    public HotelCollection hotelCollection;
    public ReviewCollection reviewCollection;
    public InvertedIndex invertedIndex;

    public FusionDataCollection() {}

    public FusionDataCollection(HotelCollection hotelCollection,
                                ReviewCollection reviewCollection, InvertedIndex invertedIndex) {
        this.hotelCollection = hotelCollection;
        this.reviewCollection = reviewCollection;
        this.invertedIndex = invertedIndex;
    }

    public void setHotelCollection(HotelCollection hotelCollection) {
        this.hotelCollection = hotelCollection;
    }

    public void setReviewCollection(ReviewCollection reviewCollection) {
        this.reviewCollection = reviewCollection;
    }

    public void setInvertedIndex(InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
    }
}
