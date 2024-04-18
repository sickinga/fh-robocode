package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;

import java.util.ArrayList;
import java.util.List;

public class ScannedRobot {

    private String _name;
    private List<Position> _positions;
    public ScannedRobot(String name) {
        _name = name;
        _positions = new ArrayList<Position>();
    }
    public void AddPosition(Position position) {
        _positions.add(position);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ScannedRobot rob) {
            if(rob._name.equals(_name)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Robot %s", _name));
        if(_positions.size() == 0){
            builder.append("- No positions");
        } else if(_positions.size() == 1){
            builder.append(String.format("- Last position %s", _positions.getLast()));
        } else if(_positions.size() > 1){
            builder.append(String.format("- Last position %s", _positions.getLast()));
            int size = _positions.size();
            Position last = _positions.get(size - 1);
            Position last2 = _positions.get(size - 2);
            Position movement = last2.substract(last);
            builder.append(String.format("- Movement (%s)", movement));
        }
        return builder.toString();
    }
}

