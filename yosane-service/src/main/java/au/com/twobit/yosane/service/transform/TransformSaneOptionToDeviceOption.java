package au.com.twobit.yosane.service.transform;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.southsky.jfreesane.OptionValueConstraintType;
import au.com.southsky.jfreesane.OptionValueType;
import au.com.southsky.jfreesane.RangeConstraint;
import au.com.southsky.jfreesane.SaneOption;
import au.com.twobit.yosane.api.DeviceOption;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class TransformSaneOptionToDeviceOption implements Function<SaneOption, DeviceOption> {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public DeviceOption apply(SaneOption option) {
        if ( option == null ) {
            return null;
        }
        DeviceOption dopt = null;
        try {
            dopt = new DeviceOption(
                    option.getName(),
                    option.getDescription(),
                    option.getTitle(),
                    option.getType().name(),
                    option.getConstraintType().name(),
                    getValueAsAString(option),
                    getRangeAsString(option));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        return dopt;
    }

    String getValueAsAString(SaneOption option) throws Exception {
        if ( option == null || option.getType() == null || !(option.isActive() && option.isWriteable()) ) {
            return null;
        }
        
        String value = null;
        try {
            switch(option.getType()) {
                case BOOLEAN:
                    value = String.valueOf( option.getBooleanValue() );
                    break;
                case BUTTON:
                    break;
                case GROUP:
                    value = option.getStringValue();
                    break;
                case STRING:
                    value = option.getStringValue();
                    break;
                case FIXED:
                    value = String.valueOf(option.getFixedValue());
                    break;
                case INT:
                    if ( option.getConstraintType() == OptionValueConstraintType.RANGE_CONSTRAINT ) {
                        value = Joiner.on(" , ").join(option.getIntegerArrayValue());
                    } else {
                        value = String.valueOf(option.getIntegerValue());
                    }
                    break;
            }
        } catch (Exception x) {
            x.printStackTrace();
            log.error( String.format("error evaluating value of Option named %s, of type %s",option.getName(), option.getType().name()));
        }
        return value;
    }
    
    List<String> getRangeAsString(SaneOption option) {
        List<String> value = Lists.newArrayList();
        
        if ( option == null ||
             option.getConstraintType() == null || 
             option.getConstraintType() == OptionValueConstraintType.NO_CONSTRAINT ||
             ! (option.isActive() && 
                option.isWriteable()) ) {
            return value;
        }
        
        try {
            RangeConstraint rc = option.getRangeConstraints();
            switch(option.getConstraintType()) {
                case VALUE_LIST_CONSTRAINT:
                    if ( option.getType() == OptionValueType.INT ) {
                        for (int i : option.getIntegerValueListConstraint()) {
                            value.add(String.valueOf(i));
                        }
                    } else if ( option.getType() == OptionValueType.FIXED ) {
                        for (double d : option.getFixedValueListConstraint()) {
                            value.add(String.valueOf(d));
                        }
                    }
                case STRING_LIST_CONSTRAINT:                    
                    value.addAll( Optional.fromNullable(option.getStringConstraints()).or(Lists.<String>newArrayList()));
                    break;
                case RANGE_CONSTRAINT:
                    if ( option.getType() == OptionValueType.INT ) {
                        value.add(String.valueOf(rc.getMinimumInteger()));
                        value.add(String.valueOf(rc.getMaximumFixed()));
                        break;
                    } else if ( option.getType() == OptionValueType.FIXED ) {
                        value.add(String.valueOf(rc.getMinimumFixed()));
                        value.add(String.valueOf(rc.getMaximumFixed()));
                        break;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception x) {
            x.printStackTrace();
            log.error( String.format("error evaluating constraints of Option named %s, with constraint type %s",option.getName(), option.getConstraintType().name()));
        }
        return value;
    }
}
