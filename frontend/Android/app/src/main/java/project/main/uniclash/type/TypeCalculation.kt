package project.main.uniclash.type

class TypeCalculation {

    fun howEffective(from: Type, to: Type): Effectiveness {
        val from2 = getTypeStrength(from)
        return calculateEffectiveness(from2, to)
    }

    fun howEffective(from: String, to: String): Effectiveness {
        val fromType = Type.valueOf(from.toUpperCase())
        val toType = Type.valueOf(to.toUpperCase())
        return howEffective(fromType, toType)
    }

    private fun getTypeStrength(type: Type): TypeStrength {
        return when (type) {
            Type.DRAGON -> TypeStrength.DRAGON
            Type.ELECTRIC -> TypeStrength.ELECTRIC
            Type.FIRE -> TypeStrength.FIRE
            Type.ICE -> TypeStrength.ICE
            Type.STONE -> TypeStrength.STONE
            Type.METAL -> TypeStrength.METAL
            Type.WATER -> TypeStrength.WATER
            else -> TypeStrength.NORMAL
        }
    }

    private fun calculateEffectiveness(from: TypeStrength, to: Type): Effectiveness {
        println("$from and $to")
        return when {
            from.effective.contains(to) -> Effectiveness.EFFECTIVE
            from.weak.contains(to) -> Effectiveness.WEAK
            else -> Effectiveness.NORMAL
        }
    }
}

enum class Type{
    DRAGON,
    ELECTRIC,
    FIRE,
    ICE,
    NORMAL,
    STONE,
    METAL,
    EMPTY,
    WATER;
}

enum class Effectiveness{
    NORMAL,
    EFFECTIVE,
    WEAK
}
enum class TypeStrength {
    DRAGON {
        override val effective = arrayListOf(Type.DRAGON)
        override val weak = arrayListOf(Type.ICE)
    },
    ELECTRIC {
        override val effective = arrayListOf(Type.WATER)
        override val weak = arrayListOf(Type.FIRE)
    },
    FIRE {
        override val effective = arrayListOf(Type.METAL,Type.STONE)
        override val weak = arrayListOf(Type.FIRE)
    },
    ICE {
        override val effective = arrayListOf(Type.DRAGON)
        override val weak = arrayListOf(Type.STONE,Type.FIRE)
    },
    NORMAL {
        override val effective = arrayListOf(Type.EMPTY)
        override val weak = arrayListOf(Type.EMPTY)
    },
    STONE {
        override val effective = arrayListOf(Type.FIRE,Type.ELECTRIC,Type.ICE)
        override val weak = arrayListOf(Type.METAL,Type.WATER)
    },
    METAL {
        override val effective = arrayListOf(Type.STONE,Type.ICE)
        override val weak = arrayListOf(Type.FIRE)
    },
    WATER {
        override val effective = arrayListOf(Type.FIRE,Type.METAL,Type.STONE)
        override val weak = arrayListOf(Type.ELECTRIC)
    };

    abstract val effective: ArrayList<Type>
    abstract val weak: ArrayList<Type>
}