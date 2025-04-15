package mod.pilot.entomophobia.systems.nest.hiveheart;

public enum StimulantType {
    Undefined,
    Idle,
    Pain,
    Alarm;

    private static final StimulantType[] _values = values();
    private static final int count = _values.length;
    public int toInt(){
        return ordinal();
    }
    public static StimulantType fromInt(int i){
        return _values[i % count];
    }
}
