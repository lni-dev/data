package me.linusdev.data.parser;

class SpaceOffsetTracker{

    private String offset;
    private final String offsetValue;

    public SpaceOffsetTracker(String offsetValue){
        offset = "";
        this.offsetValue = offsetValue;
    }

    public void add(){
        offset += offsetValue;
    }

    public void remove(){
        offset = offset.substring(0, offset.length()-offsetValue.length());
    }

    public String get(){
        return offset;
    }

    @Override
    public String toString() {
        return offset;
    }
}
