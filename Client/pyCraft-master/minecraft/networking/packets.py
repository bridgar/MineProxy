from io import BytesIO
from zlib import compress

from .types import (
    VarInt, Integer, Float, Double, Short, UnsignedShort, Long, Byte, UnsignedByte,
    String, VarIntPrefixedByteArray, Boolean, UUID, Slot, NBT, SlotArray, TripleByteArray, RecordArray
)


class PacketBuffer(object):
    def __init__(self):
        self.bytes = BytesIO()

    def send(self, value):
        """
        Writes the given bytes to the buffer, designed to emulate socket.send
        :param value: The bytes to write
        """
        self.bytes.write(value)

    def read(self, length):
        return self.bytes.read(length)

    def recv(self, length):
        return self.read(length)

    def reset(self):
        self.bytes = BytesIO()

    def reset_cursor(self):
        self.bytes.seek(0)

    def get_writable(self):
        return self.bytes.getvalue()


class PacketListener(object):
    packets_to_listen = []

    def __init__(self, callback, *args):
        self.callback = callback
        for arg in args:
            if issubclass(arg, Packet):
                self.packets_to_listen.append(arg)

    def call_packet(self, packet):
        for packet_type in self.packets_to_listen:
            if isinstance(packet, packet_type):
                self.callback(packet)


class Packet(object):
    packet_name = "base"
    id = -0x01
    definition = []

    def __init__(self, **kwargs):
        pass

    def set_values(self, **kwargs):
        for key, value in kwargs.items():
            setattr(self, key, value)
        return self

    def read(self, file_object):
        for field in self.definition:
            for var_name, data_type in field.items():
                try:
                    value = data_type.read(file_object)
                    setattr(self, var_name, value)
                except:
                    pass

    def write(self, socket, compression_threshold=None):
        # buffer the data since we need to know the length of each packet's
        # payload
        packet_buffer = PacketBuffer()
        # write packet's id right off the bat in the header
        VarInt.send(self.id, packet_buffer)

        for field in self.definition:
            for var_name, data_type in field.items():
                data = getattr(self, var_name)
                data_type.send(data, packet_buffer)

        # compression_threshold of None means compression is disabled
        if compression_threshold is not None:
            # get uncompressed length
            uncompressed_length = len(packet_buffer.get_writable())
            if uncompressed_length > compression_threshold != -1:
                # compress the current payload
                compressed_data = compress(packet_buffer.get_writable())
                packet_buffer.reset()
                # write out the length of the uncompressed payload
                VarInt.send(uncompressed_length, packet_buffer)
                # write the compressed payload itself
                packet_buffer.send(compressed_data)
            else:
                # write out a 0 to indicate uncompressed data
                packet_data = packet_buffer.get_writable()
                packet_buffer.reset()
                VarInt.send(0, packet_buffer)
                packet_buffer.send(packet_data)

        packet_data = packet_buffer.get_writable()

        VarInt.send(len(packet_data), socket)  # Packet Size
        socket.send(packet_data)  # Packet Payload


# Handshake State
# ==============
class HandShakePacket(Packet):
    id = 0x00
    packet_name = "handshake"
    definition = [
        {'protocol_version': VarInt},
        {'server_address': String},
        {'server_port': UnsignedShort},
        {'next_state': VarInt}]


STATE_HANDSHAKE_CLIENTBOUND = {

}
STATE_HANDSHAKE_SERVERBOUND = {
    0x00: HandShakePacket
}


# Status State
# ==============
class ResponsePacket(Packet):
    id = 0x00
    packet_name = "response"
    definition = [
        {'json_response': String}]


class PingPacketResponse(Packet):
    id = 0x01
    packet_name = "ping"
    definition = [
        {'time': Long}]


STATE_STATUS_CLIENTBOUND = {
    0x00: ResponsePacket,
    0x01: PingPacketResponse
}


class RequestPacket(Packet):
    id = 0x00
    packet_name = "request"
    definition = []


class PingPacket(Packet):
    id = 0x01
    packet_name = "ping"
    definition = [
        {'time': Long}]


STATE_STATUS_SERVERBOUND = {
    0x00: RequestPacket,
    0x01: PingPacket
}


# Login State
# ==============
class DisconnectPacket(Packet):
    id = 0x00
    packet_name = "disconnect"
    definition = [
        {'json_data': String}]


class EncryptionRequestPacket(Packet):
    id = 0x01
    packet_name = "encryption request"
    definition = [
        {'server_id': String},
        {'public_key': VarIntPrefixedByteArray},
        {'verify_token': VarIntPrefixedByteArray}]


class LoginSuccessPacket(Packet):
    id = 0x02
    packet_name = "login success"
    definition = [
        {'UUID': String},
        {'Username': String}]


class SetCompressionPacket(Packet):
    id = 0x03
    packet_name = "set compression"
    definition = [
        {'threshold': VarInt}]


STATE_LOGIN_CLIENTBOUND = {
    0x00: DisconnectPacket,
    0x01: EncryptionRequestPacket,
    0x02: LoginSuccessPacket,
    0x03: SetCompressionPacket
}


class LoginStartPacket(Packet):
    id = 0x00
    packet_name = "login start"
    definition = [
        {'name': String}]


class EncryptionResponsePacket(Packet):
    id = 0x01
    packet_name = "encryption response"
    definition = [
        {'shared_secret': VarIntPrefixedByteArray},
        {'verify_token': VarIntPrefixedByteArray}]


STATE_LOGIN_SERVERBOUND = {
    0x00: LoginStartPacket,
    0x01: EncryptionResponsePacket
}


# Playing State
# ==============

class SpawnObjectPacket(Packet):
    id = 0x00
    packet_name = "spawn object"
    definition = [
        {'entity_id': VarInt},
        {'object_uuid': UUID},
        {'type': Byte},
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'pitch': UnsignedByte},  # TODO should be angle
        {'yaw': UnsignedByte},  # TODO should be angle
        {'data': Integer},
        {'velocity_x': Short},
        {'velocity_y': Short},
        {'velocity_z': Short}
    ]


class SpawnExperienceOrbPacket(Packet):
    id = 0x01
    packet_name = "spawn experience orb"
    definition = [
        {'entity_id': VarInt},
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'count': Short}
    ]


class SpawnGlobalEntityPacket(Packet):
    id = 0x02
    packet_name = "spawn global entity"
    definition = [
        {'entity_id': VarInt},
        {'type': Byte},
        {'x': Double},
        {'y': Double},
        {'z': Double}
    ]


class SpawnMobPacket(Packet):
    id = 0x03
    packet_name = "spawn mob"
    definition = [
        {'entity_id': VarInt},
        {'object_uuid': UUID},
        {'type': Byte},
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'yaw': UnsignedByte},  # TODO should be angle
        {'pitch': UnsignedByte},  # TODO should be angle
        {'head_pitch': UnsignedByte},  # TODO should be angle
        {'velocity_x': Short},
        {'velocity_y': Short},
        {'velocity_z': Short}  # TODO finish this
    ]


class SpawnPaintingPacket(Packet):
    id = 0x04
    packet_name = "spawn painting"
    definition = [
        {'entity_id': VarInt},
        {'entity_uuid': UUID},
        {'title': String},
        {'location': Long},  # TODO should be position
        {'direction': Byte}
    ]


class SpawnPlayerPacket(Packet):
    id = 0x05
    packet_name = "spawn player"
    definition = [
        {'entity_id': VarInt},
        {'player_uuid': UUID},
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'yaw': UnsignedByte},  # TODO should be angle
        {'pitch': UnsignedByte}  # TODO should be angle
        # TODO finish this
    ]


class AnimationClientboundPacket(Packet):
    id = 0x06
    packet_name = "animation clientbound"
    definition = [
        {'entity_id': VarInt},
        {'animation': UnsignedByte}
    ]


class StatisticsPacket(Packet):
    id = 0x07
    packet_name = "statistics"
    definition = [
        {'statistics': VarIntPrefixedByteArray}  # TODO this is wrong
    ]


class BlockBreakAnimationPacket(Packet):
    id = 0x08
    packet_name = "block break animation"
    definition = [
        {'entity_id': VarInt},
        {'location': Long},  # TODO should be Position
        {'destroy_stage': Byte}
    ]


class UpdateBlockEntityPacket(Packet):
    id = 0x09
    packet_name = "update block entity"
    definition = [
        {'location': Long},  # TODO should be Position
        {'action': UnsignedByte},
        {'nbt_data': NBT}
    ]


class BlockActionPacket(Packet):
    id = 0x0A
    packet_name = "block action"
    definition = [
        {'location': Long},  # TODO should be Position
        {'byte_1': UnsignedByte},
        {'byte_2': UnsignedByte},
        {'block_type': VarInt}
    ]


class BlockChangePacket(Packet):
    id = 0x0B
    packet_name = "block change"
    definition = [
        {'location': Long},  # TODO should be Position
        {'block_id': VarInt}
    ]


class BossBarPacket(Packet):
    id = 0x0C
    packet_name = "boss bar"
    definition = [
        {'uuid': UUID},
        {'action': VarInt},
        {'remaining': String}
    ]


class ServerDifficultyPacket(Packet):
    id = 0x0D
    packet_name = "server difficulty"
    definition = [
        {'difficulty': UnsignedByte}
    ]


class TabCompleteClientboundPacket(Packet):
    id = 0x0E
    packet_name = "tab complete clientbound"
    definition = [
        {'count': VarInt},
        {'matches': String}
    ]


class ChatMessageClientboundPacket(Packet):
    id = 0x0F
    packet_name = "chat message"
    definition = [
        {'json_data': String},
        {'position': Byte}
    ]


class MultiBlockChangePacket(Packet):
    id = 0x10
    packet_name = "multi block change"
    definition = [
        {'chunk_x': Integer},
        {'chunk_z': Integer},
        {'records': RecordArray}
    ]


class ConfirmTransactionClientboundPacket(Packet):
    id = 0x11
    packet_name = "confirm transaction clientbound"
    definition = [
        {'window_id': Byte},
        {'action_number': Short},
        {'accepted': Boolean}
    ]


class CloseWindowClientboundPacket(Packet):
    id = 0x12
    packet_name = "close window clientbound"
    definition = [
        {'window_id': UnsignedByte}
    ]


class OpenWindowPacket(Packet):
    id = 0x13
    packet_name = "open window"
    definition = [
        {'window_id': UnsignedByte},
        {'window_type': String},
        {'window_title': String},
        {'number_of_slots': UnsignedByte},
        {'entity_id': Integer}
    ]


class WindowItemsPacket(Packet):
    id = 0x14
    packet_name = "window items"
    definition = [
        {'window_id': UnsignedByte},
        {'slots': SlotArray}
    ]


class WindowPropertyPacket(Packet):
    id = 0x15
    packet_name = "window property"
    definition = [
        {'window_id': UnsignedByte},
        {'property': Short},
        {'value': Short}
    ]


class SetSlotPacket(Packet):
    id = 0x16
    packet_name = "set slot"
    definition = [
        {'window_id': Byte},
        {'slot': Short},
        {'slot_data': Slot}
    ]


class SetCooldownPacket(Packet):
    id = 0x17
    packet_name = "set cooldown"
    definition = [
        {'item_id': VarInt},
        {'cooldown_ticks': VarInt}
    ]


class PluginMessageClientboundPacket(Packet):
    id = 0x18
    packet_name = "plugin message clientbound"
    definition = [
        {'channel': String}  # TODO finish this
    ]


class NamedSoundEffectPacket(Packet):
    id = 0x19
    packet_name = "named sound effect"
    definition = [
        {'sound_name': String},
        {'sound_category': VarInt},
        {'effect_position_x': Integer},
        {'effect_position_y': Integer},
        {'effect_position_z': Integer},
        {'volume': Float},
        {'pitch': UnsignedByte}
    ]


class DisconnectPacketPlayState(Packet):
    id = 0x1A
    packet_name = "disconnect"

    definition = [
        {'json_data': String}
    ]


class EntityStatusPacket(Packet):
    id = 0x1B
    packet_name = "entity status"
    definition = [
        {'entity_id': Integer},
        {'entity_status': Byte}
    ]


class ExplosionPacket(Packet):
    id = 0x1C
    packet_name = "explosion"
    definition = [
        {'x': Float},
        {'y': Float},
        {'z': Float},
        {'radius': Float},
        {'records': TripleByteArray},
        {'player_motion_x': Float},
        {'player_motion_y': Float},
        {'player_motion_z': Float}
    ]
    
    
class UnloadChunkPacket(Packet):
    id = 0x1D
    packet_name = "unload chunk"
    definition = [
        {'chunk_x': Integer},
        {'chunk_z': Integer}
    ]
    
    
class ChangeGameStatePacket(Packet):
    id = 0x1E
    packet_name = "change game state"
    definition = [
        {'reason': UnsignedByte},
        {'value': Float}
    ]


class KeepAliveClientboundPacket(Packet):
    id = 0x1F
    packet_name = "keep alive"
    definition = [
        {'keep_alive_id': VarInt}
    ]
    
    
class ChunkDataPacket(Packet):
    id = 0x20
    packet_name = "chunk data"
    definition = [
        {'chunk_x': Integer},
        {'chunk_z':Integer},
        {'ground_up_continuous': Boolean},
        {'primary_bit_mask': VarInt},
        {'data_and_data': VarIntPrefixedByteArray}
    ]
    
    
class EffectPacket(Packet):
    id = 0x21
    packet_name = "effect"
    definition = [
        {'effect_id': Integer},
        {'location': Long},  # TODO should be Position
        {'data': Integer},
        {'disable_relative_volume': Boolean}
    ]
    
    
class ParticlePacket(Packet):
    id = 0x22
    packet_name = "particle"
    definition = [
        {'particle_id': Integer},
        {'long_distance': Boolean},
        {'x': Float},
        {'y': Float},
        {'z': Float},
        {'offset_x': Float},
        {'offset_y': Float},
        {'offset_z': Float},
        {'particle_data': Float},
        {'particle_count': Integer}  # TODO finish this
    ]


class JoinGamePacket(Packet):
    id = 0x23
    packet_name = "join game"
    definition = [
        {'entity_id': Integer},
        {'game_mode': UnsignedByte},
        {'dimension': Integer},
        {'difficulty': UnsignedByte},
        {'max_players': UnsignedByte},
        {'level_type': String},
        {'reduced_debug_info': Boolean}
    ]
    
    
class MapPacket(Packet):
    id = 0x24
    packet_name = "map"
    definition = [
        {'item_damage': VarInt},
        {'scale': Byte},
        {'tracking_position': Boolean},
        {'icons': TripleByteArray},
        {'columns': Byte},
        {'rows': Byte},
        {'x': Byte},
        {'z': Byte},
        {'length': VarInt}  # TODO finish this
    ]
    
    
class EntityRelativeMovePacket(Packet):
    id = 0x25
    packet_name = "entity relative move"
    definition = [
        {'entity_id': VarInt},
        {'delta_x': Short},
        {'delta_y': Short},
        {'delta_z': Short},
        {'on_ground': Boolean}
    ]
    
    
class EntityLookAndRelativeMovePacket(Packet):
    id = 0x26
    packet_name = "entity look and relative move"
    definition = [
        {'entity_id': VarInt},
        {'delta_x': Short},
        {'delta_y': Short},
        {'delta_z': Short},
        {'yaw': UnsignedByte},  # TODO this should be angle
        {'pitch': UnsignedByte},  # TODO this should be angle
        {'on_ground': Boolean}
    ]


class EntityLookPacket(Packet):
    id = 0x27
    packet_name = "entity look"
    definition = [
        {'entity_id': VarInt},
        {'yaw': UnsignedByte},  # TODO this should be angle
        {'pitch': UnsignedByte},  # TODO this should be angle
        {'on_ground': Boolean}
    ]


class EntityPacket(Packet):
    id = 0x28
    packet_name = "entity"
    definition = [
        {'entity_id': VarInt}
    ]


class VehicleMoveClientboundPacket(Packet):
    id = 0x29
    packet_name = "vehicle move clientbound"
    definition = [
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'yaw': Float},
        {'pitch': Float}
    ]


class OpenSignEditorPacket(Packet):
    id = 0x2A
    packet_name = "open sign editor"
    definition = [
        {'location': Long}  # TODO this should be Position
    ]


class PlayerAbilitiesClientboundPacket(Packet):
    id = 0x2B
    packet_name = "player abilities clientbound"
    definition = [
        {'flags': Byte},
        {'flying_speed': Float},
        {'field_of_view_modifier': Float}
    ]


class CombatEventPacket(Packet):
    id = 0x2C
    packet_name = "combat event"
    definition = [
        {'event': VarInt}  # TODO finish this
    ]


class PlayerListItemPacket(Packet):
    id = 0x2D
    packet_name = "player list item"
    definition = [
        {'action': VarInt}  # TODO finish this
    ]


class PlayerPositionAndLookClientboundPacket(Packet):
    id = 0x2E
    packet_name = "player position and look"
    definition = [
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'yaw': Float},
        {'pitch': Float},
        {'flags': Byte},
        {'teleport_id': VarInt}
    ]


class UseBedPacket(Packet):
    id = 0x2F
    packet_name = "use bed"
    definition = [
        {'entity_id': VarInt},
        {'location': Long}  # TODO this should be position
    ]


class DestroyEntitiesPacket(Packet):
    id = 0x30
    packet_name = "destroy entities"
    definition = [
        # TODO finish this
    ]


class RemoveEntityEffectPacket(Packet):
    id = 0x31
    packet_name = "remove entity effect"
    definition = [
        {'entity_id': VarInt},
        {'effect_id': Byte}
    ]


class ResourcePackSendPacket(Packet):
    id = 0x32
    packet_name = "resourcepack send"
    definition = [
        {'url': String},
        {'hash': String}
    ]

class RespawnPacket(Packet):
    id = 0x33
    packet_name = "respawn"
    definition = [
        {'dimension': Integer},
        {'difficulty': UnsignedByte},
        {'gamemode': UnsignedByte},
        {'level_type': String}
    ]


class EntityHeadLookPacket(Packet):
    id = 0x34
    packet_name = "entity head look"
    definition = [
        {'entity_id': VarInt},
        {'head_yaw': UnsignedByte}  # TODO should be angle
    ]


class WorldBorderPacket(Packet):
    id = 0x35
    packet_name = "world border"
    definition = [
        {'action': VarInt}  # TODO this needs to be finished
    ]


class CameraPacket(Packet):
    id = 0x36
    packet_name = "camera"
    definition = [
        {'camera_id': VarInt}
    ]


class HeldItemChangeClientboundPacket(Packet):
    id = 0x37
    packet_name = "held item change clientbound"
    definition = [
        {'slot': Slot}
    ]


class DisplayScoreboardPacket(Packet):
    id = 0x38
    packet_name = "display scoreboard"
    definition = [
        {'position': Byte},
        {'score_name': String}
    ]


class EntityMetadataPacket(Packet):
    id = 0x39
    packet_name = "entity metadata"
    definition = [
        {'entity_id': VarInt}  # TODO finish this
    ]


class AttachEntityPacket(Packet):
    id = 0x3A
    packet_name = "attach entity"
    definition = [
        {'attached_entity_id': Integer},
        {'holding_entity_id': Integer}
    ]


class EntityVelocityPacket(Packet):
    id = 0x3B
    packet_name = "entity velocity"
    definition = [
        {'entity_id': VarInt},
        {'velocity_x': Short},
        {'velocity_y': Short},
        {'velocity_z': Short}
    ]


class EntityEquipmentPacket(Packet):
    id = 0x3C
    packet_name = "entity equipment"
    definition = [
        {'entity_id': VarInt},
        {'slot': VarInt},
        {'item': Slot}
    ]

class SetExperiencePacket(Packet):
    id = 0x3D
    packet_name = "set experience"
    definition = [
        {'experience_bar': Float},
        {'level': VarInt},
        {'total_experience': VarInt}
    ]


class UpdateHealthPacket(Packet):
    id = 0x3E
    packet_name = "update health"
    definition = [
        {'health': Float},
        {'food': VarInt},
        {'food_saturation': Float}
    ]


class ScoreboardObjectivePacket(Packet):
    id = 0x3F
    packet_name = "scoreboard objective"
    definition = [
        {'objective_name': String},
        {'mode': Byte},
        {'objective_value': String},
        {'type': String}
    ]


class SetPassengersPacket(Packet):
    id = 0x40
    packet_name = "set passengers"
    definition = [
        {'entity_id': VarInt}  # TODO finish this
    ]


class TeamsPacket(Packet):
    id = 0x41
    packet_name = "teams"
    definition = [
        {'team_name': String},
        {'mode': Byte}  # TODO finish this
    ]


class UpdateScorePacket(Packet):
    id = 0x42
    packet_name = "update score"
    definition = [
        {'score_name': String},
        {'action': Byte},
        {'objective_name': String},
        {'value': VarInt}
    ]


class SpawnPositionPacket(Packet):
    id = 0x43
    packet_name = "spawn position"
    definition = [
        {'location': Long}  # TODO should be Position
    ]


class TimeUpdatePacket(Packet):
    id = 0x44
    packet_name = "time update"
    definition = [
        {'world_age': Long},
        {'time_of_day': Long}
    ]


class TitlePacket(Packet):
    id = 0x45
    packet_name = "title"
    definition = [
        {'action': VarInt}  # TODO finish this
    ]


class UpdateSignClientboundPacket(Packet):
    id = 0x46
    packet_name = "update sign clientbound"
    definition = [
        {'location': Long},
        {'line_1': String},
        {'line_2': String},
        {'line_3': String},
        {'line_4': String}
    ]


class SoundEffectPacket(Packet):
    id = 0x47
    packet_name = "sound effect"
    definition = [
        {'sound_id': VarInt},
        {'sound_category': VarInt},
        {'effect_position_x': Integer},
        {'effect_position_y': Integer},
        {'effect_position_z': Integer},
        {'volume': Float},
        {'pitch': UnsignedByte}
    ]


class PlayerListHeaderAndFooterPacket(Packet):
    id = 0x48
    packet_name = "player list header and footer"
    definition = [
        {'header': String},
        {'footer': String}
    ]


class CollectItemPacket(Packet):
    id = 0x49
    packet_name = "collect item"
    definition = [
        {'collected_entity_id': VarInt},
        {'collector_entity_id': VarInt}
    ]


class EntityTeleportPacket(Packet):
    id = 0x4A
    packet_name = "entity teleport"
    definition = [
        {'entity_id': VarInt},
        {'x': Double},
        {'y': Double},
        {'z': Double},
        {'yaw': UnsignedByte},  # TODO should be angle
        {'pitch': UnsignedByte},  # TODO should be angle
        {'on_ground': Boolean}
    ]


class EntityPropertiesPacket(Packet):
    id = 0x4B
    packet_name = "entity properties"
    definition = [
        {'entity_id': VarInt}  # TODO finish this
    ]


class EntityEffectPacket(Packet):
    id = 0x4C
    packet_name = "entity effect"
    definition = [
        {'entity_id': VarInt},
        {'effect_id': Byte},
        {'amplifier': Byte},
        {'duration': VarInt},
        {'hide_particles', Boolean}
    ]


STATE_PLAYING_CLIENTBOUND = {
    0x00: SpawnObjectPacket,
    0x01: SpawnExperienceOrbPacket,
    0x02: SpawnGlobalEntityPacket,
    0x03: SpawnMobPacket,
    0x04: SpawnPaintingPacket,
    0x05: SpawnPlayerPacket,
    0x06: AnimationClientboundPacket,
    0x07: StatisticsPacket,
    0x08: BlockBreakAnimationPacket,
    0x09: UpdateBlockEntityPacket,
    0x0A: BlockActionPacket,
    0x0B: BlockChangePacket,
    0x0C: BossBarPacket,
    0x0D: ServerDifficultyPacket,
    0x0E: TabCompleteClientboundPacket,
    0x0F: ChatMessageClientboundPacket,
    0x10: MultiBlockChangePacket,
    0x11: ConfirmTransactionClientboundPacket,
    0x12: CloseWindowClientboundPacket,
    0x13: OpenWindowPacket,
    0x14: WindowItemsPacket,
    0x15: WindowPropertyPacket,
    0x16: SetSlotPacket,
    0x17: SetCooldownPacket,
    0x18: PluginMessageClientboundPacket,
    0x19: NamedSoundEffectPacket,
    0x1A: DisconnectPacketPlayState,
    0x1B: EntityStatusPacket,
    0x1C: ExplosionPacket,
    0x1D: UnloadChunkPacket,
    0x1E: ChangeGameStatePacket,
    0x1F: KeepAliveClientboundPacket,
    0x20: ChunkDataPacket,
    0x21: EffectPacket,
    0x22: ParticlePacket,
    0x23: JoinGamePacket,
    0x24: MapPacket,
    0x25: EntityRelativeMovePacket,
    0x26: EntityLookAndRelativeMovePacket,
    0x27: EntityLookPacket,
    0x28: EntityPacket,
    0x29: VehicleMoveClientboundPacket,
    0x2A: OpenSignEditorPacket,
    0x2B: PlayerAbilitiesClientboundPacket,
    0x2C: CombatEventPacket,
    0x2D: PlayerListItemPacket,
    0x2E: PlayerPositionAndLookClientboundPacket,
    0x2F: UseBedPacket,
    0x30: DestroyEntitiesPacket,
    0x31: RemoveEntityEffectPacket,
    0x32: ResourcePackSendPacket,
    0x33: RespawnPacket,
    0x34: EntityHeadLookPacket,
    0x35: WorldBorderPacket,
    0x36: CameraPacket,
    0x37: HeldItemChangeClientboundPacket,
    0x38: DisplayScoreboardPacket,
    0x39: EntityMetadataPacket,
    0x3A: AttachEntityPacket,
    0x3B: EntityVelocityPacket,
    0x3C: EntityEquipmentPacket,
    0x3D: SetExperiencePacket,
    0x3E: UpdateHealthPacket,
    0x3F: ScoreboardObjectivePacket,
    0x40: SetPassengersPacket,
    0x41: TeamsPacket,
    0x42: UpdateScorePacket,
    0x43: SpawnPositionPacket,
    0x44: TimeUpdatePacket,
    0x45: TitlePacket,
    0x46: UpdateSignClientboundPacket,
    0x47: SoundEffectPacket,
    0x48: PlayerListHeaderAndFooterPacket,
    0x49: CollectItemPacket,
    0x4A: EntityTeleportPacket,
    0x4B: EntityPropertiesPacket,
    0x4C: EntityEffectPacket
}


class TeleportConfirmPacket(Packet):
    id = 0x00
    packet_name = "teleport confirm"
    definition = [
        {'teleport_id': VarInt}
    ]


class KeepAliveServerboundPacket(Packet):
    id = 0x0B
    packet_name = "keep alive serverbound"
    definition = [
        {'keep_alive_id': VarInt}]


class ChatPacket(Packet):
    id = 0x02
    packet_name = "chat"
    definition = [
        {'message': String}]


class PositionAndLookPacket(Packet):
    id = 0x06
    packet_name = "position and look"
    definition = [
        {'x': Double},
        {'feet_y': Double},
        {'z': Double},
        {'yaw': Float},
        {'pitch': Float},
        {'on_ground': Boolean}]


class UseEntityPacket(Packet):
    id = 0x0A
    packet_name = "use entity"
    definition = [
        {"target": VarInt},
        {"type": VarInt},
        {"target_x": Float},

    ]


STATE_PLAYING_SERVERBOUND = {
    0x00: TeleportConfirmPacket,
    0x0B: KeepAliveServerboundPacket,
    0x02: ChatPacket,
    0x06: PositionAndLookPacket
}
