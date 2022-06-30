package victor.training.performance.primitives.interning

import java.lang.ref.WeakReference
import java.util.WeakHashMap

class MedicId(val id: Long) {
    companion object {
        private val map =  WeakHashMap<Long, WeakReference<MedicId>>()
        fun valueOf(id:Long)  = map.computeIfAbsent(id) { WeakReference(MedicId(it)) }
    }
}
typealias MedicIdX = Long
//type MedicId = Long;
//
class Visit(val patientId: Long, medic: MedicId) {}